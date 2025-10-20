package com.shdatalink.sip.server.config.web.interceptor;

import com.shdatalink.framework.common.annotation.Anonymous;
import com.shdatalink.framework.common.exception.UnAuthorizedException;
import com.shdatalink.framework.jwt.utils.JwtUtil;
import com.shdatalink.sip.server.module.user.entity.UserAccessKey;
import com.shdatalink.sip.server.module.user.service.UserAccessKeyService;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;

import java.io.IOException;

public class IntegrationInterceptor implements ContainerRequestFilter {

    @Inject
    UserAccessKeyService userAccessKeyService;
    @Context
    ResourceInfo resourceInfo;

    public final static String HEAD_PARAM_AUTHORIZATION = "access_token";


    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        // 检查是否标记为匿名访问
        if (resourceInfo.getResourceClass().isAnnotationPresent(Anonymous.class)
                || resourceInfo.getResourceMethod().isAnnotationPresent(Anonymous.class)) {
            return;
        }

        String token = request.getHeaderString(HEAD_PARAM_AUTHORIZATION);
        if (token == null) {
            throw new UnAuthorizedException();
        }
        String appKey = JwtUtil.getValueByKey(token, "userInfo");
        UserAccessKey userAccessKey = userAccessKeyService.getUserAccessKey(appKey);

        if (!JwtUtil.verify(token, appKey, userAccessKey.getSecret())) {
            throw new UnAuthorizedException();
        }
    }
}
