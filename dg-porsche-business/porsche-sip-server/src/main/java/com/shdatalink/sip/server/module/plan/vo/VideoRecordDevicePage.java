package com.shdatalink.sip.server.module.plan.vo;

import lombok.Data;

@Data
public class VideoRecordDevicePage {
    /**
     * 主键
     */
    private Integer id;
    /**
     * 设备id
     */
    private String deviceId;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 通道id
     */
    private String channelId;
    /**
     * 通道名称
     */
    private String channelName;
    /**
     * 在线状态
     */
    private Boolean online;
    /**
     * 录像状态
     */
    private Boolean recording;
}
