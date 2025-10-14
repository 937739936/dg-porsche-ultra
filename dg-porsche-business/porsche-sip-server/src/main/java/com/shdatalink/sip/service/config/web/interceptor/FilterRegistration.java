package com.shdatalink.sip.service.config.web.interceptor;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.Provider;

/**
 * 动态注册过滤器，根据资源路径自动选择合适的拦截器。
 */
@Provider
public class FilterRegistration implements DynamicFeature {

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        // 获取资源路径信息
        String path = resourceInfo.getResourceClass().getAnnotation(Path.class).value();

        // 检查路径是否匹配 /admin/** 或 /app/**，，则注册权限过滤器
        if (path.matches("admin/.*") || path.startsWith("app/.*")) {
            context.register(AuthorizationInterceptor.class);
        }
        // 检查路径是否匹配 /integration/**，则注册集成过滤器
        if (path.matches("integration/.*")) {
            context.register(IntegrationInterceptor.class);
        }
    }
}
