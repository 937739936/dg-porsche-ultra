package com.shdatalink.sip.server.module.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shdatalink.sip.server.config.mybatis.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_user_role")
public class UserRole extends BaseEntity {

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 角色ID
     */
    private Integer roleId;
}
