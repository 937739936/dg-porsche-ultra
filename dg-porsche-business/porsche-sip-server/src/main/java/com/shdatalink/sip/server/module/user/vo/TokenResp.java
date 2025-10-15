package com.shdatalink.sip.server.module.user.vo;

import lombok.Data;

@Data
public class TokenResp {

    private String token;

    private UserInfo userInfo;
}
