package com.shdatalink.sip.service.module.user.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserDetailVO {
    private Integer id;
    /**
     * 账号
     */
    private String username;
    /**
     * 姓名
     */
    private String fullName;
    /**
     * 密码
     */
    private String password;
    /**
     * md5密码盐
     */
    private String salt;
    /**
     * 删除状态(0-正常,1-已删除)
     */
    private Boolean enabled;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 备注
     */
    private String remark;
    /**
     * 第三方访问秘钥
     */
    private String integrationSecret;
    /**
     * 角色
     */
    private List<String> roleNames;
    /**
     * 角色id
     */
    private List<Integer> roleId;
}
