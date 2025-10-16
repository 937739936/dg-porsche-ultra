package com.shdatalink.sip.server.module.alarmplan.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubscribeTypeEnum {
    ALARM("Alarm","报警订阅"),
    MOBILE_POSITION( "MobilePosition","移动位置订阅"),
    CATALOG("Catalog","目录订阅"),
    ;

    private final String code;
    private final String desc;
}
