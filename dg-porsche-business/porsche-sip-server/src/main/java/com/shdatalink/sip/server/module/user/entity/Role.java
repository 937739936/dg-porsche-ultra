package com.shdatalink.sip.server.module.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shdatalink.mysql.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_role")
public class Role extends BaseEntity {

    /**
     * 角色名称
     */
    private String name;
    /**
     * 删除保护
     */
    private Boolean protect;
}
