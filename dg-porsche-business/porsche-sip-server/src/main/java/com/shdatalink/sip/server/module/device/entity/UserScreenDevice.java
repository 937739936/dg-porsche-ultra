package com.shdatalink.sip.server.module.device.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shdatalink.mysql.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_user_screen_device")
public class UserScreenDevice extends BaseEntity {
    /**
     * 预设id
     */
    private Integer presetId;
    /**
     * 设备id
     */
    private String deviceId;
    /**
     * 通道id
     */
    private String channelId;
}
