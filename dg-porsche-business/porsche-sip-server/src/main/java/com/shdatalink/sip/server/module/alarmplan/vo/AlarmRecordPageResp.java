package com.shdatalink.sip.server.module.alarmplan.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlarmRecordPageResp  {
    /**
     * 报警记录id
     */
    private Integer id;
    /**
     * 设备id
     */
    private String deviceId;
    /**
     * 通道id
     */
    private String channelId;
    /**
     * 报警级别
     */
    private String alarmPriority;
    /**
     * 报警方式
     */
    private String alarmMethod;
    /**
     * 报警时间
     */
    private LocalDateTime alarmTime;
    /**
     * 报警类型
     */
    private String alarmType;
    /**
     * 事件类型
     */
    private String eventType;
    /**
     * 图片的base64
     */
    private String base64;

}
