package com.shdatalink.sip.server.module.alarmplan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shdatalink.json.utils.JsonUtil;
import com.shdatalink.mysql.entity.BaseEntity;
import com.shdatalink.mysql.handler.IDictTypeHandler;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmMethodEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmPriorityEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmTypeEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.EventTypeEnum;
import com.shdatalink.sip.server.module.common.enums.EnableDisableEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_alarm_plan")
public class AlarmPlan extends BaseEntity {

    /**
     * 预案名称
     */
    private String name;

    /**
     * 报警级别
     */
    private String alarmPriorities;
    /**
     * 报警方式
     */
    private String alarmMethods;

    /**
     * 报警类型
     */
    private String alarmTypes;
    /**
     * 事件类型
     */
    private String eventTypes;
    /**
     * 预案状态
     */
    @TableField(typeHandler = IDictTypeHandler.class)
    private EnableDisableEnum status;

    public void addAlarmPriorities(List<AlarmPriorityEnum> alarmPriorities) {
        if(alarmPriorities == null){
            this.alarmPriorities = "[]";
            return;
        }
        this.alarmPriorities = JsonUtil.toJsonString(alarmPriorities);
    }

    public void addAlarmMethods(List<AlarmMethodEnum> alarmMethods) {
        if(alarmMethods == null){
            this.alarmMethods = "[]";
            return;
        }
        this.alarmMethods = JsonUtil.toJsonString(alarmMethods);
    }

    public void addAlarmTypes(List<AlarmTypeEnum> alarmTypes) {
        if(alarmTypes == null){
            this.alarmTypes = "[]";
            return;
        }
        this.alarmTypes = JsonUtil.toJsonString(alarmTypes);
    }

    public void addEventTypes(List<EventTypeEnum> eventTypes) {
        if(eventTypes == null){
            this.eventTypes = "[]";
            return;
        }
        this.eventTypes = JsonUtil.toJsonString(eventTypes);
    }
}
