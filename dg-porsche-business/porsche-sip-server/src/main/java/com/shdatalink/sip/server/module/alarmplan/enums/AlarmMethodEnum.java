package com.shdatalink.sip.server.module.alarmplan.enums;

import com.shdatalink.framework.common.model.IDict;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum AlarmMethodEnum implements IDict<String> {
    /**
     * 0-全部；1-电话报警；2-设备报警；3-短信报警；4-GPS报警；5-视频报警；6-设备故障报警；7-其他报警；可以为直接组合如1/2为电话报警或设备报警
     */
    TELEPHONE_ALARM("TELEPHONE_ALARM","1", "电话报警"),
    EQUIPMENT_ALARM("EQUIPMENT_ALARM", "2", "设备报警"),
    SMS_ALARM("SMS_ALARM", "3", "短信报警"),
    GPS_ALARM("GPS_ALARM", "4", "GPS报警"),
    VIDEO_ALARM("VIDEO_ALARM", "5", "视频报警"),
    EQUIPMENT_FAILURE_ALARM("EQUIPMENT_FAILURE_ALARM", "6", "设备故障报警"),
    OTHER_ALARM("OTHER_ALARM", "7", "其他报警"),
    ;

    private final String code;
    private final String val;
    private final String text;

    public static AlarmMethodEnum fromVal(String val) {
        for (AlarmMethodEnum value : AlarmMethodEnum.values()) {
            if (Objects.equals(value.getVal(), val)) {
                return value;
            }
        }
        return null;
    }

}
