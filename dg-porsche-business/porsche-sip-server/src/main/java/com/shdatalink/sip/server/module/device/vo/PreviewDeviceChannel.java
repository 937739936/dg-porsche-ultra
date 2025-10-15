package com.shdatalink.sip.server.module.device.vo;

import lombok.Data;

@Data
public class PreviewDeviceChannel {

    /**
     * 设备id（国标）
     */
    private String deviceId;
    /**
     * 通道id
     */
    private String channelId;
    /**
     * 设备名称
     */
    private String name;
    /**
     * 是否有云台控制
     */
    private boolean ptz;
    /**
     * 播放地址
     */
    private DevicePreviewPlayVO playUrl;

    /**
     * 在线状态
     */
    private Boolean online;
}