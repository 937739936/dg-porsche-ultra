package com.shdatalink.sip.server.module.alarmplan.vo;

import lombok.Data;

@Data
public class AlarmPlanChannelResp {
    /**
     * 报警预案id
     */
    private String alarmPlanId;
    /**
     * 设备id
     */
    private String deviceId;
    /**
     * 通道id
     */
    private String channelId;

}
