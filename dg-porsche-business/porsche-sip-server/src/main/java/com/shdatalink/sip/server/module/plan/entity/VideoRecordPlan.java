package com.shdatalink.sip.server.module.plan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shdatalink.mysql.entity.BaseEntity;
import lombok.Data;

@Data
@TableName("t_video_record_plan")
public class VideoRecordPlan extends BaseEntity {
    /**
     * 录像计划名称
     */
    private String name;

    /**
     * 周一计划
     */
    private Integer monday;

    /**
     * 周二计划
     */
    private Integer tuesday;

    /**
     * 周三计划
     */
    private Integer wednesday;

    /**
     * 周四计划
     */
    private Integer thursday;

    /**
     * 周五计划
     */
    private Integer friday;

    /**
     * 周六计划
     */
    private Integer saturday;

    /**
     * 周日计划
     */
    private Integer sunday;
    /**
     * 启用状态
     */
    private Boolean enabled;
}
