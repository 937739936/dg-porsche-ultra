package com.shdatalink.sip.server.module.user.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * 企业用户修改密码请求实体
 */
@Data
public class ModifyPasswordReq {

    /**
     * 原密码
     */
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;

}

