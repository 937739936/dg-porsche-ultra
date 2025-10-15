package com.shdatalink.sip.server.module.device.enums;

import com.shdatalink.framework.common.model.IDict;
import lombok.Getter;

@Getter
public enum SIPProtocolEnum implements IDict<String> {
    GBT28181("GBT28181", "国标28181"),
    PULL("PULL", "拉流"),
    RTMP("RTMP", "推流"),
    ;
    SIPProtocolEnum(String code, String text) {
        init(code, text);
    }
}
