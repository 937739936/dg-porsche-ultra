package com.shdatalink.sip.service.module.user.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
public class UserSaveParam {
    private Integer id;
    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空")
    private String username;
    /**
     * 密码
     * 添加用户时必须
     */
    private String password;
    /**
     * 姓名
     */
    @Length(max = 100)
    private String fullName;
    /**
     * 电话
     */
    @Length(max = 20)
    private String phone;
    /**
     * 邮箱
     */
    @Length(max = 100)
    private String email;
    /**
     * 备注
     */
    @Length(max = 100)
    private String remark;

    /**
     * 角色
     */
    private List<Integer> roles;
    /**
     * 启用/禁用
     */
    @NotNull
    private Boolean enabled;
}
