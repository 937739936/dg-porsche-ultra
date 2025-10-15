package com.shdatalink.sip.server.module.device.vo;

import lombok.Data;

@Data
public class PreviewDeviceList {
    /**
     * 设备id
     */
    private String deviceId;
    /**
     * 设备名称
     */
    private String name;
    /**
     * 在线状态
     */
    private Boolean online;
}
