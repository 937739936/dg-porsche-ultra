package com.shdatalink.sip.server.module.device.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shdatalink.mysql.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_user_screen")
public class UserScreen extends BaseEntity {
    /**
     * 预设名称
     */
    private String name;
    /**
     * 屏幕数量
     */
    private Integer screenCount;
}
