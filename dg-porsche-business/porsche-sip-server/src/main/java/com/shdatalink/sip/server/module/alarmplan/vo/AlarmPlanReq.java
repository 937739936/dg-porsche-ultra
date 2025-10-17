package com.shdatalink.sip.server.module.alarmplan.vo;

import com.shdatalink.sip.server.module.alarmplan.enums.AlarmMethodEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmPriorityEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmTypeEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.EventTypeEnum;
import com.shdatalink.sip.server.module.common.enums.EnableDisableEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.QueryParam;
import lombok.Data;

import java.util.List;

@Data
public class AlarmPlanReq {
    /**
     * 预案id
     */
    @QueryParam("id")
    private Integer id;
    /**
     * 预案名称
     */
    @NotBlank(message = "预案名称不能为空")
    @QueryParam("name")
    private String name;

    /**
     * 报警级别
     */
    @QueryParam("alarmPriorities")
    private List<AlarmPriorityEnum> alarmPriorities;
    /**
     * 报警方式
     */
    @QueryParam("alarmMethods")
    private List<AlarmMethodEnum> alarmMethods;

    /**
     * 报警类型
     */
    @QueryParam("alarmTypes")
    private List<AlarmTypeEnum> alarmTypes;
    /**
     * 事件类型
     */
    @QueryParam("eventTypes")
    private List<EventTypeEnum> eventTypes;
    /**
     * 预案状态
     */
    @QueryParam("status")
    private EnableDisableEnum status;


}
