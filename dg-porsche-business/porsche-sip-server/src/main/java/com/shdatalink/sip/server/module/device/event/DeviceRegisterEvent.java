package com.shdatalink.sip.server.module.device.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceRegisterEvent {
    private String deviceId;
    // true注册 false-注销
    private Boolean type;

    private Status status;

    public enum Status {
        // 认证成功
        Success,
        // 失败
        Fail
    }
}
