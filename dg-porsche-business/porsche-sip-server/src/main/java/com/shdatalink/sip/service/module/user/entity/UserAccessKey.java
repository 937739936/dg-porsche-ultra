package com.shdatalink.sip.service.module.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shdatalink.entity.BaseEntity;
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
