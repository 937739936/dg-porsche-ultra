package com.shdatalink.sip.server.module.user.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPage {
    /**
     * 用户id
     */
    private Integer id;
    /**
     * 姓名
     */
    private String fullName;
    /**
     * 用户名
     */
    private String username;
    /**
     * 启用状态
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
     * 创建时间
     */
    private LocalDateTime createdTime;
}
