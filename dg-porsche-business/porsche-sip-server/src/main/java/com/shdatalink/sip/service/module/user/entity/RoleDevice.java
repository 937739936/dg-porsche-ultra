package com.shdatalink.sip.service.module.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shdatalink.mysql.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_role_device")
public class RoleDevice extends BaseEntity {
    private Integer roleId;
    private String deviceId;
}
