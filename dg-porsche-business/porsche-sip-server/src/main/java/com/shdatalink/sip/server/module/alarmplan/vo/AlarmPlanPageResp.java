package com.shdatalink.sip.server.module.alarmplan.vo;

import com.shdatalink.sip.server.module.alarmplan.enums.AlarmMethodEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmPriorityEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmTypeEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.EventTypeEnum;
import com.shdatalink.sip.server.module.common.enums.EnableDisableEnum;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class AlarmPlanPageResp {
    /**
     * 预案id
     */
    private Integer id;
    /**
     * 预案名称
     */
    private String name;

    /**
     * 报警级别
     */
    private List<AlarmPriorityEnum> alarmPriorities;
    /**
     * 报警级别描述
     */
    private String alarmPriorityDesc;

    /**
     * 报警方式
     */
    private List<AlarmMethodEnum> alarmMethods;
    /**
     * 报警方式描述
     */
    private String alarmMethodDesc;

    /**
     * 报警类型
     */
    private List<AlarmTypeEnum> alarmTypes;
    /**
     * 报警类型描述
     */
    private String alarmTypeDesc;
    /**
     * 事件类型
     */
    private List<EventTypeEnum> eventTypes;
    /**
     * 事件类型描述
     */
    private String eventTypeDesc;
    /**
     * 预案状态
     */
    private EnableDisableEnum status;

    /**
     * 预案状态描述
     */
    private String statusDesc;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 最后修改时间
     */
    private LocalDateTime lastModifiedTime;


    public String getAlarmPriorityDesc() {
        if(CollectionUtils.isEmpty(alarmPriorities)) {
            return "全部";
        }
        return alarmPriorities.stream().map(AlarmPriorityEnum::getText).collect(Collectors.joining(","));
    }

    public String getAlarmMethodDesc() {
        if(CollectionUtils.isEmpty(alarmMethods)) {
            return "全部";
        }
        return alarmMethods.stream().map(AlarmMethodEnum::getText).collect(Collectors.joining(","));
    }

    public String getAlarmTypeDesc() {
        if(CollectionUtils.isEmpty(alarmTypes)) {
            return "全部";
        }
        return alarmTypes.stream().map(AlarmTypeEnum::getText).collect(Collectors.joining(","));
    }

    public String getEventTypeDesc() {
        if(CollectionUtils.isEmpty(eventTypes)) {
            return "全部";
        }
        return eventTypes.stream().map(EventTypeEnum::getText).collect(Collectors.joining(","));
    }

    public String getStatusDesc() {
        if(status == null){
            return "全部";
        }
        return status.getText();
    }
}
