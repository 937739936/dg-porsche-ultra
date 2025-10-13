package com.shdatalink.sip.service.module.user.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RoleDeviceSaveParam {
    /**
     * 角色id
     */
    @NotNull(message = "角色id不能为空")
    private Integer roleId;
    /**
     * 设备id
     */
    private List<String> deviceIds;
}
