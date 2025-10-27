package com.shdatalink.sip.server.module.plan.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.framework.common.utils.DateUtil;
import com.shdatalink.sip.server.gb28181.StreamFactory;
import com.shdatalink.sip.server.gb28181.core.bean.constants.InviteTypeEnum;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.RecordInfoQuery;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.response.RecordInfo;
import com.shdatalink.sip.server.gb28181.core.builder.GBRequest;
import com.shdatalink.sip.server.media.GBMediaUrl;
import com.shdatalink.sip.server.media.MediaService;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.service.DeviceChannelService;
import com.shdatalink.sip.server.module.device.service.DeviceService;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewPlayVO;
import com.shdatalink.sip.server.module.plan.event.MediaDownloadDoneEvent;
import com.shdatalink.sip.server.module.plan.vo.VideoRecordTimeLineVO;
import com.shdatalink.sip.server.utils.FFmpegUtil;
import com.shdatalink.sip.server.utils.SipUtil;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.*;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.plan.service.VideoRecordRemoteService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@ApplicationScoped
@Slf4j
public class VideoRecordRemoteService {
    @Inject
    DeviceService deviceService;
    @Inject
    DeviceChannelService deviceChannelService;
    @Inject
    MediaService mediaService;
    @Inject
    GBMediaUrl gbMediaUrl;

    public List<VideoRecordTimeLineVO> timeline(String deviceId, String channelId, LocalDate date) {
        RecordInfoQuery query = RecordInfoQuery.builder()
                .deviceId(channelId)
                .sn(SipUtil.generateSn())
                .startTime(date.atStartOfDay())
                .endTime(date.atTime(LocalTime.of(23,59,59)))
                .type("all")
                .build();
        Device device = deviceService.getByDeviceId(deviceId)
                .orElseThrow(() -> new BizException("设备不存在"));
        RecordInfo recordInfo;
        try {
            recordInfo = GBRequest.message(device.toGbDevice())
                    .execute(query)
                    .get();
        } catch (Exception e) {
            throw new BizException("设备端没有录像");
        }

        return recordInfo.getRecordList()
                .stream()
                .map(item -> {
                    long startTime = item.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    long endTime = item.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    return new VideoRecordTimeLineVO(startTime, Long.valueOf(endTime-startTime).intValue());
                }).toList();
    }

    public DevicePreviewPlayVO playback(String deviceId, String channelId, LocalDateTime start) {
        DeviceChannel channel = deviceChannelService.findByDeviceIdAndChannelId(deviceId, channelId).orElseThrow(() -> new BizException("通道不存在"));
        Device device = deviceService.getByDeviceId(channel.getDeviceId()).orElseThrow(() -> new BizException("设备不存在"));
        return mediaService.getPlaybackUrl(device, channel, start);
    }

    public void download(String deviceId, String channelId, LocalDateTime startTime, LocalDateTime endTime, RoutingContext context) throws ExecutionException, InterruptedException, TimeoutException, IOException {
        DeviceChannel channel = deviceChannelService.findByDeviceIdAndChannelId(deviceId, channelId).orElseThrow(() -> new BizException("通道不存在"));
        String streamId = StreamFactory.streamId(InviteTypeEnum.Download, channel.getId().toString());
        if (mediaService.rtpServerExists(streamId)) {
            throw new BizException("该设备有其他下载任务，请稍后再试");
        }

        String downloadUrl = gbMediaUrl.download(channel.getId(), startTime, endTime);
        String codec = FFmpegUtil.probeCodec(downloadUrl);
        if (StringUtils.isBlank(codec)) {
            log.error("未能获取到视频格式, 下载失败, {}", downloadUrl);
            throw new BizException("下载失败");
        }

        String[] cmd;
        if (codec.equals("hevc")) {
            cmd  = new String[]{
                    "-i", downloadUrl,
                    "-c:v", "copy",
                    "-c:a", "aac",
                    "-tag:v", "hvc1",
                    "-movflags", "frag_keyframe+empty_moov",
                    "-f", "mp4"
            };
        } else {
            cmd  = new String[]{
                    "-i", downloadUrl,
                    "-c:v", "copy",
                    "-c:a", "aac",
                    "-movflags", "frag_keyframe+empty_moov",
                    "-f", "mp4"
            };
        }

        HttpServerResponse response = context.response();
        response.putHeader("Content-Disposition", "attachment; filename=record.mp4");
        response.putHeader("Content-Type", "video/mp4");
        response.setChunked(true);
        long timeout = DateUtil.betweenSeconds(startTime, endTime) + 60;
        try {
            FFmpegUtil.pipe(
                    (int) timeout,
                    (bytes) -> {
                        response.write(Buffer.buffer(bytes));
                    },
                    cmd
            );
        } catch (Exception e) {
            stopDownload(deviceId, channelId);
        }

    }

    public void downloadDone(@ObservesAsync MediaDownloadDoneEvent event) {
        DeviceChannel channel = deviceChannelService.getBaseMapper().selectByChannelId(event.getChannelId());
        stopDownload(channel.getDeviceId(), channel.getChannelId());
    }

    public void stopDownload(String deviceId, String channelId) {
        deviceChannelService.stop(InviteTypeEnum.Download, deviceId, channelId);
    }
}
