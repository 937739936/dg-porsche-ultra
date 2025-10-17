package com.shdatalink.sip.server.integration.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IntegrationChannelOnlineStatusVO {
    private String channelId;
    private String deviceId;
    private boolean online;
    private LocalDateTime offlineTime;
}
