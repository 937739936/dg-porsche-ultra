package com.shdatalink.sip.server.module.alarmplan.vo;

import com.shdatalink.sip.server.module.alarmplan.enums.AlarmMethodEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmPriorityEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmTypeEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.EventTypeEnum;
import com.shdatalink.sip.server.module.common.enums.EnableDisableEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class AlarmPlanReq {
    /**
     * 预案id
     */
    private Integer id;
    /**
     * 预案名称
     */
    @NotBlank(message = "预案名称不能为空")
    private String name;

    /**
     * 报警级别
     */
    private List<AlarmPriorityEnum> alarmPriorities;
    /**
     * 报警方式
     */
    private List<AlarmMethodEnum> alarmMethods;

    /**
     * 报警类型
     */
    private List<AlarmTypeEnum> alarmTypes;
    /**
     * 事件类型
     */
    private List<EventTypeEnum> eventTypes;
    /**
     * 预案状态
     */
    private EnableDisableEnum status;


}
