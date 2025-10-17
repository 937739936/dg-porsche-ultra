package com.shdatalink.sip.server.integration.vo;

import lombok.Data;

@Data
public class IntegrationAccessTokenVO {
    private Integer expiresIn;
    private String accessToken;
}
