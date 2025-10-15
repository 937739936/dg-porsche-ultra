package com.shdatalink.sip.server.module.user.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RolePermissionSaveParam {
    /**
     * 角色id
     */
    @NotNull(message = "角色id不能为空")
    private Integer roleId;
    /**
     * 权限id
     */
    private List<Integer> permissionIds;
}
