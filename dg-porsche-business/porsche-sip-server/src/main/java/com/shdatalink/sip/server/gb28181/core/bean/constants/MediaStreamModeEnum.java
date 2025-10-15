package com.shdatalink.sip.server.gb28181.core.bean.constants;

import com.shdatalink.framework.common.model.IDict;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MediaStreamModeEnum implements IDict<String> {
    UDP("UDP", "UDP", TransportTypeEnum.UDP),
    TCP_ACTIVE("TCP_ACTIVE", "TCP-ACTIVE", TransportTypeEnum.TCP),
    TCP_PASSIVE("TCP_PASSIVE", "TCP-PASSIVE", TransportTypeEnum.TCP);


    private final TransportTypeEnum transportType;
    MediaStreamModeEnum(String code, String text, TransportTypeEnum transportType) {
        init(code, text);
        this.transportType = transportType;
    }

}
