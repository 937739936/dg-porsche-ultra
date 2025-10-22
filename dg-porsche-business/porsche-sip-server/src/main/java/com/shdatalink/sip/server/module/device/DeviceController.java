package com.shdatalink.sip.server.module.device;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shdatalink.framework.common.annotation.Anonymous;
import com.shdatalink.framework.common.annotation.CheckPermission;
import com.shdatalink.framework.common.annotation.IgnoredResultWrapper;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.sip.server.media.MediaService;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import com.shdatalink.sip.server.module.device.service.*;
import com.shdatalink.sip.server.module.device.valid.Gb28181Valid;
import com.shdatalink.sip.server.module.device.valid.PullValid;
import com.shdatalink.sip.server.module.device.valid.RtmpValid;
import com.shdatalink.sip.server.module.device.vo.*;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.SseEventSink;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.reactive.RestForm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 设备管理
 */
@Path("admin/device")
public class DeviceController {

    @Inject
    DeviceService deviceService;
    @Inject
    DeviceChannelService deviceChannelService;
    @Inject
    SseClient sseClient;
    @Inject
    DeviceLogService deviceLogService;
    @Inject
    MediaService mediaService;
    @Inject
    Validator validator;

    /**
     * 添加设备
     * @param param 参数
     * @return
     */
    @CheckPermission("device:add")
    @POST
    @Path("add")
    public String addDevice(@Valid DeviceAddParam param) {
        Set constraintViolations = validator.validate(param, validateGroup(param.getProtocolType()).toArray(new Class[]{}));
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }
        return deviceService.addDeviceConfig(param);
    }

    public List<Class> validateGroup(ProtocolTypeEnum protocolType){
        List<Class> validateGroup = new ArrayList<>();
        if(protocolType == ProtocolTypeEnum.GB28181){
            validateGroup.add(Gb28181Valid.class);
        }else if(protocolType == ProtocolTypeEnum.PULL){
            validateGroup.add(PullValid.class);
        }else if(protocolType == ProtocolTypeEnum.RTMP){
            validateGroup.add(RtmpValid.class);
        }
        return validateGroup;
    }

    /**
     * 修改设备
     * @param param
     * @return
     */
    @CheckPermission("device:edit")
    @POST
    @Path("update")
    public boolean updateDevice(@Valid DeviceUpdateParam param) {
        Set constraintViolations = validator.validate(param, validateGroup(param.getProtocolType()).toArray(new Class[]{}));
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }
        return deviceService.update(param);
    }

    /**
     * 设备列表
     *
     * @param param
     * @return
     */
    @CheckPermission("device:list")
    @GET
    @Path("page")
    public IPage<DevicePage> page(DevicePageParam param) {
        return deviceService.getPage(param);
    }

    /**
     * 导入设备
     */
    @CheckPermission("device:import")
    @POST
    @Path("importDevice")
    public List<String> importDevice(@RestForm File file) throws Exception {
        return deviceService.importDevice(file);
    }

    /**
     * 设备日志
     */
    @GET
    @Path("logs")
    public IPage<DeviceLogsPage> logs(DeviceLogsPageParam param) {
        return deviceLogService.getPage(param);
    }

    /**
     * 通道列表
     */
    @GET
    @Path("channel/page")
    public IPage<DeviceChannelPage> channelPage(@Valid DeviceChannelPageParam param) {
        return deviceChannelService.getPage(param);
    }

    /**
     * 查询播放链接
     */
    @GET
    @Path("playUrl")
    public DevicePreviewPlayVO playUrl(@QueryParam("deviceId") @NotBlank String deviceId, @QueryParam("channelId") @NotBlank String channelId) {
        Device device = deviceService.getByDeviceId(deviceId).orElseThrow(() -> new BizException("设备'" + deviceId + "'不存在"));
        DeviceChannel channel = deviceChannelService.findByDeviceIdAndChannelId(deviceId, channelId).orElseThrow(() -> new BizException("通道不存在"));
        return mediaService.getPlayUrl(device, channel);
    }

    /**
     * 刷新通道
     * @param deviceId 设备id（国标）
     */
    @GET
    @Path("channel/refresh")
    public Integer channelRefresh(@QueryParam("deviceId") @NotBlank String deviceId) {
        Device device = deviceService.getByDeviceId(deviceId)
                .orElseThrow(() -> new BizException("设备'" + deviceId + "'不存在"));
        if(device.getProtocolType() == ProtocolTypeEnum.PULL || device.getProtocolType() == ProtocolTypeEnum.RTMP){
            throw new BizException("该协议类型设备不支持刷新通道");
        }
        return deviceChannelService.renewalChannel(deviceId);
    }

    /**
     * 修改通道名称
     */
    @POST
    @Path("modifyChannelName")
    public boolean modifyChannelName(@Valid DeviceChannelNameModifyParam param) {
        DeviceChannel channel = deviceChannelService.findByDeviceIdAndChannelId(param.getDeviceId(), param.getChannelId())
                .orElseThrow(() -> new BizException("设备通道不存在"));
        channel.setName(param.getName());
        deviceChannelService.updateById(channel);
        return true;
    }

    /**
     * 修改云台类型
     */
    @POST
    @Path("modifyChannelPtzType")
    public boolean modifyChannelPtzType(@Valid DevicePtzTypeModifyParam param) {
        DeviceChannel channel = deviceChannelService.findByDeviceIdAndChannelId(param.getDeviceId(), param.getChannelId())
                .orElseThrow(() -> new BizException("设备通道不存在"));
        channel.setPtzType(param.getPtzType());
        deviceChannelService.updateById(channel);
        return true;
    }

    /**
     * sse订阅
     * <p>订阅不同事件时，返回的数据结构不同：</p>
     * <p>Online 类型: {@link DeviceSSEResponse.Online}</p>
     * <p>Log 类型: {@link DeviceSSEResponse.MessageLog}</p>
     * <p>RegisterInfoUpdate 类型: {@link DevicePage}</p>
     */
    @GET
    @Path("sse")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @IgnoredResultWrapper
    public void createSseEmitter(@Context SseEventSink eventSink) {
        sseClient.createSse(eventSink, UUID.randomUUID().toString());
    }

    /**
     * 设备强制下线/上线
     * @return
     */
    @GET
    @Path("switchEnable")
    public boolean switchEnable(@QueryParam("deviceId") @NotBlank String deviceId, @QueryParam("enable") @NotNull Boolean enable) {
        return deviceService.switchEnable(deviceId, enable);
    }

    /**
     * 删除设备
     * @param deviceId 设备国标id
     * @return
     */
    @DELETE
    @Path("delete")
    public boolean delete(@QueryParam("deviceId") @NotBlank String deviceId) {
        return deviceService.delete(deviceId);
    }

    /**
     * 删除通道
     * @param deviceId 设备国标id
     * @param channelId 通道id
     * @return
     */
    @DELETE
    @Path("channel/delete")
    public boolean deleteChannel(@QueryParam("deviceId") @NotBlank String deviceId, @QueryParam("channelId") @NotBlank String channelId) {
        return deviceChannelService.delete(deviceId, channelId);
    }

    /**
     * 查询设备名称
     * @param deviceId
     * @param channelId
     * @return
     */
    @GET
    @Path("queryName")
    public DeviceNameVO queryName(@QueryParam("deviceId") @NotBlank String deviceId, @QueryParam("channelId") String channelId) {
        DeviceNameVO vo = new DeviceNameVO();
        vo.setDeviceName(deviceService.getByDeviceId(deviceId).orElseThrow(() -> new BizException("设备不存在")).getName());
        if (StringUtils.isNotBlank(channelId)) {
            vo.setChannelName(deviceChannelService.findByDeviceIdAndChannelId(deviceId, channelId).orElseThrow(() -> new BizException("通道不存在")).getName());
        }
        return vo;
    }

}
