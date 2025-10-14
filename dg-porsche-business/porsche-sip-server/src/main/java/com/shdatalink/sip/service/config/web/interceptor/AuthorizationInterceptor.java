package com.shdatalink.sip.service.config.web.interceptor;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;

import java.io.IOException;

/**
 * 授权拦截器，用于检查请求的合法性。
 */
public class AuthorizationInterceptor implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // 实现授权逻辑
        String path = requestContext.getUriInfo().getPath();
        System.out.println("Authorization filtering: " + path);

        // 示例：如果未授权，返回401
        // if (!isAuthorized(requestContext)) {
        //     requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        // }
    }
}
