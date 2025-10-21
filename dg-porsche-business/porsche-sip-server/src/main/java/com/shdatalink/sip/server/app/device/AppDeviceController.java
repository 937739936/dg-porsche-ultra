package com.shdatalink.sip.server.app.device;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shdatalink.sip.server.app.device.service.AppDeviceChannelService;
import com.shdatalink.sip.server.app.device.service.AppDeviceService;
import com.shdatalink.sip.server.app.device.vo.*;
import com.shdatalink.sip.server.module.device.vo.DeviceNameVO;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewSnapshot;
import com.shdatalink.sip.server.module.device.vo.PtzControlParam;
import com.shdatalink.sip.server.module.device.vo.PtzControlStopParam;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * APP/设备管理
 */
@Path("app/device")
public class AppDeviceController {

    @Inject
    AppDeviceService appDeviceService;
    @Inject
    AppDeviceChannelService appDeviceChannelService;

    /**
     * 设备列表
     * @param param
     * @return
     */
    @Path("page")
    @GET
    public IPage<AppDevicePage> page(AppDevicePageParam param) {
        return appDeviceService.getPage(param);
    }

    /**
     * 通道列表
     */
    @Path("channel/page")
    @GET
    public IPage<AppDeviceChannelPage> channelPage(@Valid AppDeviceChannelPageParam param) {
        return appDeviceChannelService.getPage(param);
    }


    /**
     * 通道详情
     */
    @Path("channel/detail")
    @GET
    public AppDeviceChannelDetailVO channelPage(@QueryParam("deviceId") @NotBlank String deviceId,
                                                @QueryParam("channelId") @NotBlank String channelId) {
        return appDeviceChannelService.detail(deviceId, channelId);
    }

    /**
     * 查询快照
     *
     * @param deviceId 设备id（国标）
     * @return
     */
    @Path("snapshot")
    @GET
    public DevicePreviewSnapshot getSnapshot(@QueryParam("deviceId") @NotBlank String deviceId,
                                             @QueryParam("channelId") @NotBlank String channelId) throws IOException {
        return appDeviceChannelService.snapShot(deviceId, channelId);
    }

    /**
     * 云台控制
     * @param param
     * @return
     */
    @Path("ptzControl")
    @POST
    public boolean ptzControl(@Valid PtzControlParam param) {
        return appDeviceService.ptzControl(param);
    }

    /**
     * 云台控制开始
     * @param param
     * @return
     */
    @Path("ptzControlStart")
    @POST
    public boolean ptzControlStart(@Valid PtzControlParam param) {
        return appDeviceService.ptzControlStart(param);
    }

    /**
     * 云台控制停止
     * @param param
     * @return
     */
    @Path("ptzControlStop")
    @POST
    public boolean ptzControlStop(@Valid PtzControlStopParam param) {
        return appDeviceService.ptzControlStop(param);
    }

    /**
     * 查询设备名称
     * @param deviceId
     * @param channelId
     * @return
     */
    @Path("queryName")
    @GET
    public DeviceNameVO queryName(@QueryParam("deviceId") @NotBlank String deviceId, @QueryParam("deviceId") String channelId) {
        DeviceNameVO vo = new DeviceNameVO();
        vo.setDeviceName(appDeviceService.getBaseMapper().selectByDeviceId(deviceId).getName());
        if (StringUtils.isNotBlank(channelId)) {
            vo.setChannelName(appDeviceService.getBaseMapper().selectByChannelId(channelId).getName());
        }
        return vo;
    }
}
