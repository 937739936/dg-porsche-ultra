package com.shdatalink.sip.server.integration.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IntegrationDevicePreviewSnapshot {
    /**
     * 通道id
     */
    private String channelId;
    /**
     * 快照创建时间
     */
    private LocalDateTime createTime;
    /**
     * 图片的base64
     */
    private String base64;
}
