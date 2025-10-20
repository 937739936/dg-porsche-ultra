package com.shdatalink.sip.server.integration;

import com.shdatalink.framework.common.annotation.Anonymous;
import com.shdatalink.framework.jwt.utils.JwtUtil;
import com.shdatalink.sip.server.integration.vo.IntegrationAccessTokenVO;
import com.shdatalink.sip.server.module.user.entity.UserAccessKey;
import com.shdatalink.sip.server.module.user.service.UserAccessKeyService;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import java.util.Objects;

/**
 * 外部接口/令牌
 */
@Path("integration/token")
public class IntegrationTokenController {

    @Inject
    UserAccessKeyService userAccessKeyService;

    /**
     * 根据appid生成访问令牌，2小时内有效
     * @param accessKey accessKey
     * @param secret 秘钥
     * @return
     */
    @Path("appId")
    @GET
    @Anonymous
    public IntegrationAccessTokenVO generateByAppId(@QueryParam("accessKey") @NotBlank String accessKey, @QueryParam("secret") @NotBlank String secret) {
        UserAccessKey userAccessKey = userAccessKeyService.getUserAccessKey(accessKey);
        if (userAccessKey == null) {
            throw new ForbiddenException();
        }

        if (!Objects.equals(secret, userAccessKey.getSecret())) {
            throw new ForbiddenException();
        }

        String sign = JwtUtil.sign(accessKey, secret, 7200000L);
        IntegrationAccessTokenVO vo = new IntegrationAccessTokenVO();;
        vo.setExpiresIn(7200);
        vo.setAccessToken(sign);
        return vo;
    }
}
