package com.shdatalink.sip.server.integration;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shdatalink.sip.server.integration.service.IntegrationDeviceChannelService;
import com.shdatalink.sip.server.integration.service.IntegrationDeviceService;
import com.shdatalink.sip.server.integration.vo.*;
import com.shdatalink.sip.server.module.plan.vo.VideoRecordTimeLineVO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 外部接口/设备
 */
@Path("integration/device")
public class IntegrationDeviceController {
    @Inject
    IntegrationDeviceService integrationDeviceService;
    @Inject
    IntegrationDeviceChannelService integrationDeviceChannelService;

    /**
     * 根据设备名称查询
     * @param name
     * @return
     */
    @Path("queryByName")
    @GET
    public List<IntegrationDeviceDetail> queryByName(@QueryParam("name") @NotBlank String name) {
        return integrationDeviceService.queryByName(name);
    }

    /**
     * 通道列表
     */
    @Path("channel")
    @GET
    public IPage<IntegrationDeviceChannelList> channel(@Valid IntegrationChannelPageParam param) {
        return integrationDeviceChannelService.getPage(param);
    }

    /**
     * 查询快照
     * @param channelId 通道id
     * @return
     */
    @Path("snapshot")
    @GET
    public List<IntegrationDevicePreviewSnapshot> getSnapshot(@QueryParam("channelId") @NotEmpty List<String> channelId) {
        return integrationDeviceChannelService.getSnapshot(channelId);
    }

    /**
     * 设备在线状态查询
     * @param deviceId 通道id
     */
    @Path("queryDeviceOnline")
    @GET
    public List<IntegrationChannelOnlineStatusVO> queryDeviceOnline(@QueryParam("deviceId") @NotEmpty List<String> deviceId) {
        return integrationDeviceService.queryOnline(deviceId);
    }

    /**
     * 通道在线状态查询
     * @param channelId 通道id
     */
    @Path("queryChannelOnline")
    @GET
    public List<IntegrationChannelOnlineStatusVO> queryChannelOnline(@QueryParam("channelId") @NotEmpty List<String> channelId) {
        return integrationDeviceChannelService.queryOnline(channelId);
    }

    /**
     * 云台控制(每次点击移动一秒)
     * @param param
     * @return
     */
    @Path("ptzControl")
    @POST
    public boolean ptzControl(@Valid IntegrationPtzControlParam param) {
        return integrationDeviceChannelService.ptzControl(param);
    }

    /**
     * 云台控制-开始(按住不放)
     * @param param
     * @return
     */
    @Path("ptzControlStart")
    @POST
    public boolean ptzControlStart(@Valid IntegrationPtzControlStartParam param) {
        return integrationDeviceChannelService.ptzControlStart(param);
    }

    /**
     * 云台控制-开始(松手)
     * @param param
     * @return
     */
    @Path("ptzControlStop")
    @POST
    public boolean ptzControlStop(@Valid IntegrationPtzControlStopParam param) {
        return integrationDeviceChannelService.ptzControlStop(param);
    }

    /**
     * 查询直播url
     * @param channelId 通道id
     * @return
     */
    @Path("playUrl")
    @GET
    public List<IntegrationDevicePreviewPlayVO> playUrl(@QueryParam("channelId") @NotEmpty List<String> channelId) {
        return integrationDeviceChannelService.playUrl(channelId);
    }

    /**
     * 回放url查询
     * @param channelId 通道id
     * @param start 开始时间
     * @return
     */
    @Path("playbackUrl")
    @GET
    public IntegrationDevicePreviewPlayVO playbackUrl(@QueryParam("channelId") @NotBlank String channelId,
                                                      @QueryParam("start") @NotNull LocalDateTime start
    ) {
        return integrationDeviceChannelService.playbackUrl(channelId, start);
    }

    /**
     * 回放时间线
     *
     * @param channelId 通道id
     * @param date      日期
     * @return
     */
    @Path("playbackTimeline")
    @GET
    public List<VideoRecordTimeLineVO> playbackTimeline(@QueryParam("channelId") @NotBlank  String channelId,
                                                        @QueryParam("date") @NotNull LocalDate date
    ) {
        return integrationDeviceChannelService.playbackTimeline(channelId, date);
    }
}
