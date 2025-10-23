package com.shdatalink.sip.server.module.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shdatalink.sip.server.config.mybatis.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_user_access_key")
public class UserAccessKey extends BaseEntity {
    private Integer userId;
    private String accessKey;
    private String secret;
}
