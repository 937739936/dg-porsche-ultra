package com.shdatalink.sip.server.module.device.enums;

import com.shdatalink.framework.common.model.IDict;
import lombok.Getter;

@Getter
public enum MessageTypeEnum implements IDict<String> {
    // 注册消息
    Register("Register", "注册"),
    // 上线消息
    Online("Online", "上线/离线"),
    // 通道更新
    RenewalChannel("RenewalChannel", "通道更新"),
    // 流消息
    Stream("Stream", "流消息"),
    ;
    MessageTypeEnum(String code, String text) {
        init(code, text);
    }
}
