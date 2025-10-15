package com.shdatalink.sip.server.module.device.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.shdatalink.sip.server.module.device.enums.MessageTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_device_log")
public class DeviceLog {
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 设备id
     */
    private String deviceId;
    /**
     * 通道id
     */
    private String channelId;
    /**
     * sip协议类型
     */
    private MessageTypeEnum type;
    /**
     * 日期
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    /**
     * 日志内容
     */
    private String content;
    /**
     * 在线状态
     */
    private Boolean online;
}
