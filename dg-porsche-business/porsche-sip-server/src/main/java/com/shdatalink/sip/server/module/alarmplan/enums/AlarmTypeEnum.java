package com.shdatalink.sip.server.module.alarmplan.enums;

import com.shdatalink.framework.common.model.IDict;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum AlarmTypeEnum implements IDict<String> {
    /**
     * 1-视频丢失报警；2-设备防拆报警；3-存储设备磁盘满报警；4-设备高温报警；5-设备低温报警
     * 1-人工视频报警；2-运动目标检测报警；3-遗留物检测报警；4-物体移除检测报警；5-绊线检测报警；6-入侵检测报警；7-逆行检测报警；8-徘徊检测报警；9-流量统计报警；10-密度检测报警；11-视频异常检测报警；12-快速移动报警；13-图像遮挡报警。
     */
    VIDEO_LOSS_ALARM("VIDEO_LOSS_ALARM","1", "视频丢失报警", AlarmMethodEnum.EQUIPMENT_ALARM),
    DEVICE_TAMPER_ALARM("DEVICE_TAMPER_ALARM", "2", "设备防拆报警", AlarmMethodEnum.EQUIPMENT_ALARM),
    STORAGE_DEVICE_DISK_FULL_ALARM("STORAGE_DEVICE_DISK_FULL_ALARM", "3", "存储设备磁盘满报警", AlarmMethodEnum.EQUIPMENT_ALARM),
    DEVICE_HIGH_TEMPERATURE_ALARM("DEVICE_HIGH_TEMPERATURE_ALARM", "4", "设备高温报警", AlarmMethodEnum.EQUIPMENT_ALARM),
    DEVICE_LOW_TEMPERATURE_ALARM("DEVICE_LOW_TEMPERATURE_ALARM", "5", "设备低温报警", AlarmMethodEnum.EQUIPMENT_ALARM),
    MANUAL_VIDEO_ALARM("MANUAL_VIDEO_ALARM","1", "人工视频报警", AlarmMethodEnum.VIDEO_ALARM),
    MOVING_TARGETS_DETECTION_ALARM("MOVING_TARGETS_DETECTION_ALARM", "2", "运动目标检测报警", AlarmMethodEnum.VIDEO_ALARM),
    RESIDUAL_ITEM_DETECTION_ALARM("RESIDUAL_ITEM_DETECTION_ALARM", "3", "遗留物检测报警", AlarmMethodEnum.VIDEO_ALARM),
    OBJECT_REMOVAL_DETECTION_ALARM("OBJECT_REMOVAL_DETECTION_ALARM", "4", "物体移除检测报警", AlarmMethodEnum.VIDEO_ALARM),
    TRIPPING_WIRE_DETECTION_ALARM("TRIPPING_WIRE_DETECTION_ALARM", "5", "绊线检测报警", AlarmMethodEnum.VIDEO_ALARM),
    INTRUSION_DETECTION_ALARM("INTRUSION_DETECTION_ALARM", "6", "入侵检测报警", AlarmMethodEnum.VIDEO_ALARM),
    RETROGRADE_DETECTION_ALARM("RETROGRADE_DETECTION_ALARM", "7", "逆行检测报警", AlarmMethodEnum.VIDEO_ALARM),
    HOVER_DETECTION_ALARM("HOVER_DETECTION_ALARM", "8", "徘徊检测报警", AlarmMethodEnum.VIDEO_ALARM),
    FLOW_STATISTICS_ALARM("FLOW_STATISTICS_ALARM", "9", "流量统计报警", AlarmMethodEnum.VIDEO_ALARM),
    DENSITY_DETECTION_ALARM("DENSITY_DETECTION_ALARM", "10", "密度检测报警", AlarmMethodEnum.VIDEO_ALARM),
    VIDEO_ANOMALY_DETECTION_ALARM("VIDEO_ANOMALY_DETECTION_ALARM","11", "视频异常检测报警", AlarmMethodEnum.VIDEO_ALARM),
    RAPID_MOVEMENT_ALARM("RAPID_MOVEMENT_ALARM", "12", "快速移动报警", AlarmMethodEnum.VIDEO_ALARM),
    IMAGE_OCCLUSION_ALARM("IMAGE_OCCLUSION_ALARM", "13", "图像遮挡报警", AlarmMethodEnum.VIDEO_ALARM),
    ;

    private final String code;
    private final String val;
    private final String text;
    private final AlarmMethodEnum alarmMethod;

    public static AlarmTypeEnum fromVal(String val, AlarmMethodEnum alarmMethod) {
        for (AlarmTypeEnum value : AlarmTypeEnum.values()) {
            if (Objects.equals(value.getVal(), val) && value.getAlarmMethod() ==  alarmMethod) {
                return value;
            }
        }
        return null;
    }

}
