package com.shdatalink.sip.server.module.alarmplan.enums;

import com.shdatalink.framework.common.model.IDict;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum EventTypeEnum implements IDict<String> {
    /**
     * 进入区域
     * 离开区域
     */
    IN_AREA("IN_AREA", "1", "进入区域"),
    OUT_AREA("OUT_AREA", "2", "离开区域"),
    ;

    private final String code;

    private final String val;

    private final String text;


    public static EventTypeEnum fromVal(String val) {
        for (EventTypeEnum value : EventTypeEnum.values()) {
            if (Objects.equals(value.getVal(), val)) {
                return value;
            }
        }
        return null;
    }


}
