package com.shdatalink.sip.server.module.plan.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VideoRecordPlanSaveParam  {
    private Integer id;
    /**
     * 录像计划名称
     */
    @NotBlank
    private String name;

    /**
     * 周一计划
     */
    @NotBlank
    private String monday;

    /**
     * 周二计划
     */
    @NotBlank
    private String tuesday;

    /**
     * 周三计划
     */
    @NotBlank
    private String wednesday;

    /**
     * 周四计划
     */
    @NotBlank
    private String thursday;

    /**
     * 周五计划
     */
    @NotBlank
    private String friday;

    /**
     * 周六计划
     */
    @NotBlank
    private String saturday;

    /**
     * 周日计划
     */
    @NotBlank
    private String sunday;
}
