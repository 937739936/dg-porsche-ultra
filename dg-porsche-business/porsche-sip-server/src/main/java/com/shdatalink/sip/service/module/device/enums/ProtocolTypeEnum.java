package com.shdatalink.sip.service.module.device.enums;

import com.shdatalink.framework.common.model.IDict;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProtocolTypeEnum implements IDict<String> {
    /**
     * PULL、RTMP、GB28181
     */
    PULL("PULL", "PULL", "PULL"),
    RTMP("RTMP", "PUSH", "RTMP"),
    GB28181("GB28181", "GB", "GB28181");

    private final String code;

    private final String abbr;

    private final String text;
}
