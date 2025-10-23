package com.shdatalink.sip.server.module.alarmplan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shdatalink.sip.server.config.mybatis.BaseEntity;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmMethodEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmPriorityEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmTypeEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.EventTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_alarm_record")
public class AlarmRecord extends BaseEntity {

    /**
     * 命令序列号
     */
    private String sn;
    /**
     * 设备编码
     */
    private String deviceId;
    /**
     * 通道id
     */
    private String channelId;
    /**
     * 报警级别
     */
    private AlarmPriorityEnum alarmPriority;
    /**
     * 报警方式
     */
    private AlarmMethodEnum alarmMethod;
    /**
     * 报警时间
     */
    private LocalDateTime alarmTime;
    /**
     * 报警类型
     */
    private AlarmTypeEnum alarmType;
    /**
     * 事件类型
     */
    private EventTypeEnum eventType;
    /**
     * 附件路径
     */
    private String filePath;

}
