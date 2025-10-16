package com.shdatalink.sip.server.module.alarmplan.enums;

import com.shdatalink.framework.common.model.IDict;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum AlarmPriorityEnum implements IDict<String> {
    /**
     * 1-一级警情；2-二级警情；3-三级警情；4-四级警情
     */
    ONE_LEVEL_ALARM("ONE_LEVEL_ALARM", "1", "一级警情"),
    TWO_LEVEL_ALARM("TWO_LEVEL_ALARM", "2", "二级警情"),
    THREE_LEVEL_ALARM("THREE_LEVEL_ALARM", "3", "三级警情"),
    FOUR_LEVEL_ALARM("FOUR_LEVEL_ALARM", "4", "四级警情"),
    ;

    private final String code;

    private final String val;

    private final String text;


    public static AlarmPriorityEnum fromVal(String val) {
        for (AlarmPriorityEnum value : AlarmPriorityEnum.values()) {
            if (Objects.equals(value.getVal(), val)) {
                return value;
            }
        }
        return null;
    }

}
