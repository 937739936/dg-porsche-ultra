package com.shdatalink.sip.server.module.user.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleSaveParam {
    /**
     * 角色id
     */
    private Integer id;
    /**
     * 名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String name;
}
