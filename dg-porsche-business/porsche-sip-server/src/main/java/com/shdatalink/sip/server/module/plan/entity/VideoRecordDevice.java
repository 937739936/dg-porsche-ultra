package com.shdatalink.sip.server.module.plan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shdatalink.framework.mysql.entity.BaseEntity;
import lombok.Data;

@Data
@TableName("t_video_record_device")
public class VideoRecordDevice extends BaseEntity {
    /**
     * 录像计划id
     */
    private Integer planId;
    /**
     * 设备id
     */
    private String deviceId;
    /**
     * 通道id
     */
    private String channelId;
}
