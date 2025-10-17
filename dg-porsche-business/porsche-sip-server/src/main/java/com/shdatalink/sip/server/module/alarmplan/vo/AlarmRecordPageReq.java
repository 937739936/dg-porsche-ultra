package com.shdatalink.sip.server.module.alarmplan.vo;

import com.shdatalink.sip.server.common.dto.PageParam;
import com.shdatalink.sip.server.common.dto.PageParamWithGet;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmMethodEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmPriorityEnum;
import jakarta.ws.rs.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlarmRecordPageReq extends PageParamWithGet {
    /**
     * 设备id
     */
    @QueryParam("deviceId")
    private String deviceId;
    /**
     * 预警开始时间
     */
    @QueryParam("startTime")
    private LocalDateTime startTime;
    /**
     * 预警结束时间
     */
    @QueryParam("endTime")
    private LocalDateTime endTime;

    /**
     * 报警级别
     */
    @QueryParam("alarmPriority")
    private AlarmPriorityEnum alarmPriority;
    /**
     * 报警方式
     */
    @QueryParam("alarmMethod")
    private AlarmMethodEnum alarmMethod;

}
