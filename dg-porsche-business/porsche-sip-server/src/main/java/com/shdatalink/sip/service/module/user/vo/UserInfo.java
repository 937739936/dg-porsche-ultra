package com.shdatalink.sip.service.module.user.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserInfo {

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
    @JsonIgnore
    private String password;
    /**
     * md5密码盐
     */
    @JsonIgnore
    private String salt;
    /**
     * 删除状态(0-正常,1-已删除)
     */
    private Boolean enabled;
    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 最后修改人
     */
    private String lastModifiedBy;
    /**
     * 最后修改时间
     */
    private LocalDateTime lastModifiedTime;
    private List<Integer> roleIds;
    private List<String> permissionTokens;
    private List<String> deviceIds;
}
