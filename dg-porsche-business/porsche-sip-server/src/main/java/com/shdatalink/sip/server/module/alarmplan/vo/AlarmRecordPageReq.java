package com.shdatalink.sip.server.module.alarmplan.vo;

import com.shdatalink.sip.server.common.dto.PageParam;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmMethodEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmPriorityEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlarmRecordPageReq extends PageParam {
    /**
     * 设备id
     */
    @RestQuery
    private String deviceId;
    /**
     * 预警开始时间
     */
    @RestQuery
    private LocalDateTime startTime;
    /**
     * 预警结束时间
     */
    @RestQuery
    private LocalDateTime endTime;

    /**
     * 报警级别
     */
    @RestQuery
    private AlarmPriorityEnum alarmPriority;
    /**
     * 报警方式
     */
    @RestQuery
    private AlarmMethodEnum alarmMethod;

}
