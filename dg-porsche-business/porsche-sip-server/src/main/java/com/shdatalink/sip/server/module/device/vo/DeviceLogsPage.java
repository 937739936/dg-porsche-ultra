package com.shdatalink.sip.server.module.device.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeviceLogsPage {
    /**
     * 时间
     */
    private LocalDateTime createTime;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 在线状态
     */
    private Boolean online;
}
