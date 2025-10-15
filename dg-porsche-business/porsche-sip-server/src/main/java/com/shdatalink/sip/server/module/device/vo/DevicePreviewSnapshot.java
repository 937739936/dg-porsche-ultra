package com.shdatalink.sip.server.module.device.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DevicePreviewSnapshot {
    /**
     * 快照创建时间
     */
    private LocalDateTime createTime;
    /**
     * 图片的base64
     */
    private String base64;
}
