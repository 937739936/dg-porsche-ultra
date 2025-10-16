package com.shdatalink.sip.server.module.device;

import com.shdatalink.framework.common.annotation.Anonymous;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewPlayVO;
import com.shdatalink.sip.server.module.plan.service.VideoRecordRemoteService;
import com.shdatalink.sip.server.module.plan.service.VideoRecordService;
import com.shdatalink.sip.server.module.plan.vo.VideoRecordTimeLineVO;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.DateFormat;
import org.jboss.resteasy.reactive.RestHeader;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * 录像回放/录像回放
 */
@Path("admin/device/record")
public class DeviceRecordController {

    @Inject
    VideoRecordService videoRecordService;
    @Inject
    VideoRecordRemoteService videoRecordRemoteService;

    /**
     * 录像回放-时间线
     *
     * @param deviceId  设备id
     * @param channelId 通道id
     * @param date      日期
     * @param type      local:本地 remote:远端
     * @return
     */
    @GET
    @Path("timeline")
    public List<VideoRecordTimeLineVO> timeline(@RestQuery String deviceId, @RestQuery String channelId,
                                                @RestQuery @DateFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                @RestQuery String type) {
        if (type.equals("local")) {
            return videoRecordService.timeline(deviceId, channelId, date);
        } else {
            return videoRecordRemoteService.timeline(deviceId, channelId, date);
        }
    }

    /**
     * 设置倍速
     *
     * @param deviceId
     * @param channelId
     * @param speed     倍速，支持0.5,1,2,4
     * @return
     */
    @Path("speed")
    @GET
    public boolean setSpeed(@RestQuery String deviceId, @RestQuery String channelId, @RestQuery String ssrc, @RestQuery float speed) {
        return videoRecordService.setSpeed(deviceId, channelId, ssrc, speed);
    }

    /**
     * 录像回放url
     *
     * @param deviceId  设备id
     * @param channelId 通道id
     * @param start     开始播放时间
     * @param type      local:本地 remote:远端
     * @return
     */
    @Path("playbackUrl")
    @GET
    public DevicePreviewPlayVO playback(@RestQuery String deviceId,
                                        @RestQuery String channelId,
                                        @RestQuery @DateFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                        @RestQuery String type
    ) throws IOException, InterruptedException {
        if (type.equals("remote")) {
            return videoRecordRemoteService.playback(deviceId, channelId, start);
        } else {
            return videoRecordService.playback(deviceId, channelId, start);
        }
    }

    /**
     * m3u8链接
     *
     * @param deviceId
     * @param channelId
     * @param start
     * @param end
     * @return
     */
    @Path("hls.m3u8")
    @GET
    @Anonymous
    public Response m3u8(@RestQuery String deviceId, @RestQuery String channelId,
                         @RestQuery @DateFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                         @RestQuery @DateFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end
    ) throws IOException, InterruptedException {
        return videoRecordService.m3u8(deviceId, channelId, start, end);
    }

    /**
     * ts下载地址
     */
    @Path("ts/{recordId}/{offset}/{diff}/{merge}/{fileName}")
    @GET
    @Anonymous
    public void ts(@PathParam("recordId") Integer recordId,
                   @PathParam("offset") Long offset,
                   @PathParam("diff") Float diff,
                   @PathParam("merge") String merge,
                   @PathParam("fileName") String fileName,
                   @HeaderParam(value = "range") String range,
                   RoutingContext context
    ) throws IOException, InterruptedException {
        videoRecordService.ts(recordId, offset, diff, merge, fileName, range, context);
    }

    /**
     * 获取预估下载时长
     *
     * @param deviceId
     * @param channelId
     * @param start
     * @param end
     * @param type
     * @return
     */
    @Path("downloadTime")
    @GET
    public Long downloadTime(@RestQuery String deviceId,
                             @RestQuery String channelId,
                             @RestQuery @DateFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                             @RestQuery @DateFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                             @RestQuery String type) {
        if (type.equals("remote")) {
            return videoRecordRemoteService.downloadTime(deviceId, channelId, start, end);
        } else {
            return 0L;
        }
    }

    /**
     * 下载mp4文件
     *
     * @param deviceId
     * @param channelId
     * @param start
     * @param end
     * @param type      local:本地 remote:远端
     * @throws IOException
     */
    @Path("download")
    @GET
    public void download(@RestQuery String deviceId,
                         @RestQuery String channelId,
                         @RestQuery @DateFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                         @RestQuery @DateFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                         @RestQuery String type,
                         RoutingContext context
    ) throws IOException, InterruptedException, TimeoutException, ExecutionException {
        if (type.equals("local")) {
            videoRecordService.download(deviceId, channelId, start, end, context);
        } else {
            videoRecordRemoteService.download(deviceId, channelId, start, end, context);
        }
    }

    /**
     * 停止下载
     *
     * @param deviceId
     * @param channelId
     * @param type
     */
    @Path("stopDownload")
    @GET
    public void stopDownload(@RestQuery String deviceId,
                             @RestQuery String channelId,
                             @RestQuery String type
    ) {
        if (type.equals("local")) {
        } else {
            videoRecordRemoteService.stopDownload(deviceId, channelId);
        }
    }

}
