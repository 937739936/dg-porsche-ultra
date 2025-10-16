package com.shdatalink.sip.server.module.alarmplan.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeviceEventEnum {

    ADD("添加设备"),
    UPDATE("更新设备"),
    DEL("删除设备"),
    ON("设备上线"),
    OFF("设备下线"),
    ;
    private final String desc;
}
