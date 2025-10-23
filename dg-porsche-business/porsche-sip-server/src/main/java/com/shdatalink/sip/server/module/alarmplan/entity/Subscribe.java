package com.shdatalink.sip.server.module.alarmplan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shdatalink.sip.server.module.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_subscribe")
public class Subscribe extends BaseEntity {

    /**
     * 设备编码
     */
    private String deviceId;

    /**
     * 是否订阅目录
     */
    private boolean catalog;
    /**
     * 是否订阅报警
     */
    private boolean alarm;
    /**
     * 是否订阅位置
     */
    private boolean position;
    /**
     * 订阅目录过期时间
     */
    private LocalDateTime catalogTime;
    /**
     * 订阅报警过期时间
     */
    private LocalDateTime alarmTime;
    /**
     * 订阅位置过期时间
     */
    private LocalDateTime positionTime;
}
