package com.shdatalink.sip.server.module.device.vo;

import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import com.shdatalink.sip.server.module.device.enums.PtzTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeviceChannelPage {
    /**
     * 设备配置ID
     */
    private Integer id;
    /**
     * 设备名称
     */
    private String name;
    /**
     * 设备id（国标id）
     */
    private String deviceId;
    /**
     * 通道id
     */
    private String channelId;

    /**
     * 云台类型
     */
    private PtzTypeEnum ptzType;
    private String ptzTypeText;
    public String getPtzTypeText() {
        if (ptzType == null) {
            return null;
        }
        return ptzType.getText();
    }

    /**
     * id是否已经关联设备
     */
    private Boolean enable;

    /**
     * 设备id关联设备时间
     */
    private LocalDateTime registerTime;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    /**
     * 更新时间
     */
    private LocalDateTime lastModifiedTime;
    /**
     * 是否在线
     */
    private Boolean online;
    /**
     * 是否在播放中
     */
    private Boolean living;
    /**
     * 是否有云台控制
     */
    private boolean ptz;
    public Boolean getPtz() {
        return PtzTypeEnum.PTZCamera == ptzType;
    }

    /**
     * 协议类型
     */
    private ProtocolTypeEnum protocolType;

    public static DeviceChannelPage fromDeviceChannel(DeviceChannel deviceChannel) {
        DeviceChannelPage deviceChannelPage = new DeviceChannelPage();
        deviceChannelPage.setId(deviceChannel.getId());
        deviceChannelPage.setName(deviceChannel.getName());
        deviceChannelPage.setDeviceId(deviceChannel.getDeviceId());
        deviceChannelPage.setChannelId(deviceChannel.getChannelId());
        deviceChannelPage.setPtzType(deviceChannel.getPtzType());
        deviceChannelPage.setEnable(deviceChannel.getEnable());
        deviceChannelPage.setCreatedTime(deviceChannel.getCreatedTime());
        deviceChannelPage.setLastModifiedTime(deviceChannel.getLastModifiedTime());
        deviceChannelPage.setOnline(deviceChannel.getOnline());
        deviceChannelPage.setPtz(PtzTypeEnum.PTZCamera.equals(deviceChannel.getPtzType()));
        return deviceChannelPage;
    }
}
