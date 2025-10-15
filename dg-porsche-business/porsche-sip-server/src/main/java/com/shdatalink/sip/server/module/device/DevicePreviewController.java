package com.shdatalink.sip.server.module.device;

import com.shdatalink.sip.server.module.device.service.DeviceChannelService;
import com.shdatalink.sip.server.module.device.service.DeviceService;
import com.shdatalink.sip.server.module.device.service.UserScreenService;
import com.shdatalink.sip.server.module.device.vo.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
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

    /**
     * 设备列表树
     * @return
     */
    @GET
    @Path("deviceList")
    public List<PreviewDeviceList> deviceList() {
        return deviceService.previewDeviceList();
    }

    /**
     * 设备通道
     * @return
     */
    @GET
    @Path("channelList")
    public List<PreviewDeviceChannel> channelList(@QueryParam("deviceId") String deviceId) {
        return deviceService.previewChannels(deviceId);
    }

    /**
     * 播放参数信息查询
     * @param deviceId 设备id
     * @param channelId 通道id
     */
    @GET
    @Path("info")
    public DevicePreviewInfoVO info(@QueryParam("deviceId") String deviceId, @QueryParam("channelId") String channelId) {
//        return deviceChannelService.info(deviceId, channelId);
        return null;
    }

    /**
     * 查询快照
     * @param deviceId 设备id（国标）
     * @return
     */
    @GET
    @Path("snapshot")
    public DevicePreviewSnapshot getSnapshot(@QueryParam("deviceId") String deviceId, @QueryParam("channelId") String channelId) throws IOException {
//        return deviceChannelService.snapShot(deviceId, channelId);
        return null;
    }

    /**
     * 查询实时快照
     * @param deviceId 设备id（国标）
     * @return
     */
    @GET
    @Path("realTimeSnap")
    public DevicePreviewSnapshot realTimeSnap(@QueryParam("deviceId") String deviceId, @QueryParam("channelId") String channelId) throws IOException {
//        return deviceChannelService.realTimeSnap(deviceId, channelId);
        return null;
    }

    /**
     * 查询预设详情
     * @param id
     * @return
     */
    @GET
    @Path("getPreset")
    public DevicePreviewPresetVO getPreset(@QueryParam("id") Integer id) {
        return userScreenService.getDetail(id);
    }

    /**
     * 保存预设组
     * @param param
     * @return
     */
    @POST
    @Path("setPreset")
    public boolean setPreset(@Valid DevicePreviewPresetParam param) {
        return userScreenService.save(param);
    }

    /**
     * 预设列表
     * 只返回最近的10条
     * @return
     */
    @GET
    @Path("presetList")
    public List<PreviewPresetListVO> getPresetList() {
        return userScreenService.getList();
    }

    /**
     * 删除预设
     * @param id
     * @return
     */
    @DELETE
    @Path("presetDelete")
    public boolean deletePreset(@QueryParam("id") Integer id) {
        return userScreenService.delete(id);
    }

    /**
     * 云台控制
     * @param param
     * @return
     */
    @POST
    @Path("ptzControl")
    public boolean ptzControl(@Valid PtzControlParam param) {
        return deviceService.ptzControl(param);
    }

    /**
     * 云台控制开始
     * @param param
     * @return
     */
    @POST
    @Path("ptzControlStart")
    public boolean ptzControlStart(@Valid PtzControlParam param) {
        return deviceService.ptzControlStart(param);
    }

    /**
     * 云台控制停止
     * @param param
     * @return
     */
    @POST
    @Path("ptzControlStop")
    public boolean ptzControlStop(@Valid PtzControlStopParam param) {
        return deviceService.ptzControlStop(param);
    }
}
