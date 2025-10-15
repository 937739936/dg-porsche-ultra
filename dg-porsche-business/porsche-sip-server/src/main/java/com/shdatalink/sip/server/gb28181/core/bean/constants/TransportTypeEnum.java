package com.shdatalink.sip.server.gb28181.core.bean.constants;

import com.shdatalink.framework.common.model.IDict;
import lombok.Getter;

@Getter
public enum TransportTypeEnum implements IDict<String> {
    UDP("UDP", "UDP"),
    TCP("TCP", "TCP"),
    ;

    TransportTypeEnum(String code, String text) {
        init(code, text);
    }

    public static TransportTypeEnum parse(String code) {
        for (TransportTypeEnum item : values()) {
            if (item.getCode().equalsIgnoreCase(code)) {
                return item;
            }
        }
        return null;
    }
}
