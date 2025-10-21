package com.shdatalink.sip.server.app.device.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppDevicePreviewSnapshot {
    /**
     * 快照创建时间
     */
    private LocalDateTime createTime;
    /**
     * 图片的base64
     */
    private String base64;
}
