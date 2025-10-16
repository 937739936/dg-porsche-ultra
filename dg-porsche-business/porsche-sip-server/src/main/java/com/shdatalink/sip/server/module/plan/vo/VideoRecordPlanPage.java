package com.shdatalink.sip.server.module.plan.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoRecordPlanPage {
    /**
     * id
     */
    private Integer id;
    /**
     * 名称
     */
    private String name;
    /**
     * 是否启用
     */
    private Boolean enabled;
    /**
     * 创建日期
     */
    private LocalDateTime createdTime;
    /**
     * 修改日期
     */
    private LocalDateTime lastModifiedTime;
}
