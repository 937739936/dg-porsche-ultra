package com.shdatalink.sip.server.module.alarmplan.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.QueryParam;
import lombok.Data;

@Data
public class SubscribeReq {
    /**
     * 目录
     */
    @QueryParam("catalog")
    private boolean catalog;
    /**
     * 报警
     */
    @QueryParam("alarm")
    private boolean alarm;
    /**
     * 位置
     */
    @QueryParam("position")
    private boolean position;

    /**
     * 设备id
     */
    @NotBlank(message = "设备id不能为空")
    @QueryParam("deviceId")
    private String deviceId;


}
