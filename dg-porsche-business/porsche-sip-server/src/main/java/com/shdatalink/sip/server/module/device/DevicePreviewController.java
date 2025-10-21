package com.shdatalink.sip.server.module.device;

import com.shdatalink.sip.server.module.device.service.DeviceChannelService;
import com.shdatalink.sip.server.module.device.service.DeviceService;
import com.shdatalink.sip.server.module.device.service.DeviceSnapService;
import com.shdatalink.sip.server.module.device.service.UserScreenService;
import com.shdatalink.sip.server.module.device.vo.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;


import java.io.IOException;
import java.util.List;

/**
 * 设备调阅
 */
@Path("admin/device/preview")
public class DevicePreviewController {

    @Inject
    DeviceService deviceService;
    @Inject
    UserScreenService userScreenService;
    @Inject
    DeviceChannelService deviceChannelService;
    @Inject
    DeviceSnapService deviceSnapService;

    /**
     * 设备列表树
     */
    @GET
    @Path("deviceList")
    public List<PreviewDeviceList> deviceList() {
        return deviceService.previewDeviceList();
    }

    /**
     * 设备通道
     */
    @GET
    @Path("channelList")
    public List<PreviewDeviceChannel> channelList(@QueryParam("deviceId") @NotBlank String deviceId) {
        return deviceService.previewChannels(deviceId);
    }

    /**
     * 播放参数信息查询
     * @param deviceId 设备id
     * @param channelId 通道id
     */
    @GET
    @Path("info")
    public DevicePreviewInfoVO info(@QueryParam("deviceId") @NotBlank String deviceId, @QueryParam("channelId") @NotBlank String channelId) {
        return deviceChannelService.info(deviceId, channelId);
    }

    /**
     * 查询快照
     * @param deviceId 设备id（国标）
     */
    @GET
    @Path("snapshot")
    public DevicePreviewSnapshot getSnapshot(@QueryParam("deviceId") @NotBlank String deviceId, @QueryParam("channelId") @NotBlank String channelId) throws IOException {
        return deviceSnapService.querySnapshot(deviceId, channelId);
    }

    /**
     * 查询实时快照
     * @param deviceId 设备id（国标）
     */
    @GET
    @Path("realTimeSnap")
    public DevicePreviewSnapshot realTimeSnap(@QueryParam("deviceId") @NotBlank String deviceId, @QueryParam("channelId") @NotBlank String channelId) throws IOException {
        return deviceSnapService.realTimeSnap(deviceId, channelId);
    }

    /**
     * 查询预设详情
     */
    @GET
    @Path("getPreset")
    public DevicePreviewPresetVO getPreset(@QueryParam("id") @NotNull Integer id) {
        return userScreenService.getDetail(id);
    }

    /**
     * 保存预设组
     */
    @POST
    @Path("setPreset")
    public boolean setPreset(@Valid DevicePreviewPresetParam param) {
        return userScreenService.save(param);
    }

    /**
     * 预设列表
     * 只返回最近的10条
     */
    @GET
    @Path("presetList")
    public List<PreviewPresetListVO> getPresetList() {
        return userScreenService.getList();
    }

    /**
     * 删除预设
     */
    @DELETE
    @Path("presetDelete")
    public boolean deletePreset(@QueryParam("id") @NotNull Integer id) {
        return userScreenService.delete(id);
    }

    /**
     * 云台控制
     */
    @POST
    @Path("ptzControl")
    public boolean ptzControl(@Valid PtzControlParam param) {
        return deviceService.ptzControl(param);
    }

    /**
     * 云台控制开始
     */
    @POST
    @Path("ptzControlStart")
    public boolean ptzControlStart(@Valid PtzControlParam param) {
        return deviceService.ptzControlStart(param);
    }

    /**
     * 云台控制停止
     */
    @POST
    @Path("ptzControlStop")
    public boolean ptzControlStop(@Valid PtzControlStopParam param) {
        return deviceService.ptzControlStop(param);
    }
}
