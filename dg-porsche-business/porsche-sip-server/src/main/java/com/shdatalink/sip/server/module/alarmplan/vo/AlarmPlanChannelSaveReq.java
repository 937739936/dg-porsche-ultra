package com.shdatalink.sip.server.module.alarmplan.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AlarmPlanChannelSaveReq {
    /**
     * 报警预案id
     */
    @NotNull(message = "报警预案id不能为空")
    private Integer planId;
    /**
     * 通道id
     */
    private List<String> channelIds;

}
