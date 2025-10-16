package com.shdatalink.sip.server.module.alarmplan.vo;

import lombok.Data;

@Data
public class SubscribeResp {
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

}
