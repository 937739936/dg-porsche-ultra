package com.shdatalink.web.filter;


import com.shdatalink.framework.common.annotation.IgnoredResultWrapper;
import com.shdatalink.framework.common.model.ResultWrapper;
import com.shdatalink.json.utils.JsonUtil;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;

import java.lang.reflect.Method;

@Provider
public class ControllerReturnValueHandler implements ContainerResponseFilter {

    // 注入 JAX-RS 标准的 ResourceInfo，获取当前请求的方法和类信息
    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        // 1. 判断当前请求的方法或类是否标注了 @IgnoredResultWrapper,存在注解，直接返回原始响应
        if (isSkip()) {
            return;
        }

        // 2. 无注解则执行统一包装
        Object entity = responseContext.getEntity();
        if (!(entity instanceof ResultWrapper)) {
            responseContext.setEntity(JsonUtil.toJsonString(ResultWrapper.success(entity)));
        }
    }

    /**
     * 判断是否需要跳过包装
     */
    private boolean isSkip() {
        // 获取当前请求的方法
        Method method = resourceInfo.getResourceMethod();
        // 检查方法是否标注了 @IgnoredResultWrapper
        if (method != null && method.isAnnotationPresent(IgnoredResultWrapper.class)) {
            return true;
        }

        // 获取当前请求的类
        Class<?> resourceClass = resourceInfo.getResourceClass();
        // 检查类是否标注了 @IgnoredResultWrapper
        return resourceClass != null && resourceClass.isAnnotationPresent(IgnoredResultWrapper.class);
    }
}
