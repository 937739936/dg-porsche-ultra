package com.shdatalink.sip.server.module.device.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备上下线事件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceOnlineEvent {
    private String deviceId;
    private String channelId;
    private Boolean online;

    public DeviceOnlineEvent(String deviceId, Boolean online) {
        this.deviceId = deviceId;
        this.online = online;
    }
}
