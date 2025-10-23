package com.shdatalink.sip.server.module.device.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shdatalink.sip.server.config.mybatis.BaseEntity;
import com.shdatalink.sip.server.module.device.enums.PtzTypeEnum;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_device_channel")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceChannel extends BaseEntity {

    /**
     * 通道名称
     */
    private String name;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 通道id
     */
    private String channelId;


    /**
     * 是否启用
     */
    private Boolean enable;

    /**
     * 是否在线
     */
    private Boolean online;
    /**
     * 正在录像
     */
    private Boolean recording;

    /**
     * 注册时间
     */
    private LocalDateTime registerTime;
    /**
     * 云台类型
     */
    private PtzTypeEnum ptzType;
    /**
     * 离线时间
     */
    private LocalDateTime leaveTime;

}

