package com.shdatalink.sip.server.module.alarmplan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shdatalink.sip.server.config.mybatis.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_alarm_plan_channel_rel")
public class AlarmPlanChannelRel extends BaseEntity {

    /**
     * 报警预案id
     */
    private Integer alarmPlanId;
    /**
     * 设备id
     */
    private String deviceId;
    /**
     * 通道id
     */
    private String channelId;


}
