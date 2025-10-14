package com.shdatalink.sip.service.config.web.interceptor;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

public class IntegrationInterceptor implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // 实现集成相关逻辑
        String path = requestContext.getUriInfo().getPath();
        System.out.println("Integration filtering: " + path);
    }
}
