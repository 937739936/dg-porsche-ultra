package com.shdatalink.sip.server.module.plan.service;

import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.framework.common.utils.DateUtil;
import com.shdatalink.sip.server.config.SipConfigProperties;
import com.shdatalink.sip.server.gb28181.StreamFactory;
import com.shdatalink.sip.server.gb28181.core.bean.constants.InviteTypeEnum;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.RecordInfoQuery;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.response.RecordInfo;
import com.shdatalink.sip.server.gb28181.core.builder.GBRequest;
import com.shdatalink.sip.server.media.MediaService;
import com.shdatalink.sip.server.media.MediaUrlService;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.service.DeviceChannelService;
import com.shdatalink.sip.server.module.device.service.DeviceService;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewPlayVO;
import com.shdatalink.sip.server.module.plan.vo.VideoRecordTimeLineVO;
import com.shdatalink.sip.server.utils.SipUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@ApplicationScoped
public class VideoRecordRemoteService {
    @Inject
    DeviceService deviceService;
    @Inject
    DeviceChannelService deviceChannelService;
    @Inject
    MediaUrlService mediaUrlService;
    @Inject
    SipConfigProperties sipConfigProperties;
    @Inject
    MediaService mediaService;

    private static final Map<String, CompletableFuture<String>> downloadFutureMap = new ConcurrentHashMap<>();

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
                    .newSession()
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
        return mediaUrlService.playBackUrl(deviceId, channelId, channel.getId().toString(), start);
    }

    public void download(String deviceId, String channelId, LocalDateTime startTime, LocalDateTime endTime, RoutingContext context) throws ExecutionException, InterruptedException, TimeoutException, IOException {
        DeviceChannel channel = deviceChannelService.findByDeviceIdAndChannelId(deviceId, channelId).orElseThrow(() -> new BizException("通道不存在"));
        String streamId = StreamFactory.streamId(InviteTypeEnum.Download, channel.getId().toString());
        Device device = deviceService.getByDeviceId(channel.getDeviceId()).orElseThrow(() -> new BizException("设备不存在"));
        if (mediaService.rtpServerExists(streamId)) {
            throw new BizException("该设备有其他下载任务，请稍后再试");
        }

        int port = mediaService.openRtpServer(streamId);

        GBRequest.invite(device.toGbDevice(channel.getChannelId()))
                .withStreamId(streamId)
                .withMediaAddress(sipConfigProperties.server().wanIp(), port)
                .download(startTime, endTime)
                .execute();

        CompletableFuture<String> future = new CompletableFuture<>();
        downloadFutureMap.put(streamId, future);
        String filePath = future.get(DateUtil.betweenSeconds(startTime, endTime)+60, TimeUnit.SECONDS);
        try (FileInputStream inputStream = new FileInputStream(filePath)) {
            context.response().putHeader("Content-Disposition", "attachment; filename=record.mp4");
            context.response().putHeader("Content-Type", "video/mp4");
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                context.response().write(Buffer.buffer(Arrays.copyOfRange(buffer, 0, bytesRead)));
            }
        } catch (FileNotFoundException e) {
            throw new BizException("下载失败，文件未找到");
        } catch (IOException e) {
            throw new BizException("下载失败");
        }
        Files.deleteIfExists(Paths.get(filePath));
    }

    public void downloadDone(String streamId, String filePath) {
        CompletableFuture<String> completableFuture = downloadFutureMap.get(streamId);
        if (completableFuture != null) {
            completableFuture.complete(filePath);
        }
    }

    public Long downloadTime(String deviceId, String channelId, LocalDateTime start, LocalDateTime end) {
        RecordInfoQuery query = RecordInfoQuery.builder()
                .deviceId(channelId)
                .sn(SipUtil.generateSn())
                .startTime(start)
                .endTime(end)
                .type("all")
                .build();
        Device device = deviceService.getByDeviceId(deviceId)
                .orElseThrow(() -> new BizException("设备不存在"));
        RecordInfo recordInfo;
        try {
            recordInfo = GBRequest.message(device.toGbDevice())
                    .newSession()
                    .execute(query)
                    .get();
        } catch (Exception e) {
            throw new BizException("设备端没有录像");
        }
        Long totalSeconds = recordInfo.getRecordList()
                .stream()
                .map(item -> DateUtil.betweenSeconds(item.getStartTime(), item.getEndTime()))
                .reduce(Long::sum)
                .get();
        return (totalSeconds/4)+30;
    }

    public void stopDownload(String deviceId, String channelId) {
        deviceChannelService.stop(InviteTypeEnum.Download, deviceId, channelId);
    }
}
