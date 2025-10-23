package com.shdatalink.sip.server.module.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shdatalink.sip.server.module.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_permission")
public class Permission extends BaseEntity {

    /**
     * 权限节点名称（用于前端展示）
     */
    private String name;

    /**
     * 权限标识（用于后端校验）
     */
    private String permission;

    /**
     * 父级权限ID
     */
    private Integer parentId;

    /**
     * 排序号
     */
    private Integer sort;
}
