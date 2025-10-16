package com.shdatalink.sip.server.module.alarmplan.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubscribeReq {
    /**
     * 目录
     */
    private boolean catalog;
    /**
     * 报警
     */
    private boolean alarm;
    /**
     * 位置
     */
    private boolean position;

    /**
     * 设备id
     */
    @NotBlank(message = "设备id不能为空")
    private String deviceId;


}
