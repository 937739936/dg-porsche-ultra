package com.shdatalink.framework.web.filter;


import com.shdatalink.framework.common.annotation.IgnoredResultWrapper;
import com.shdatalink.framework.common.model.ResultWrapper;
import com.shdatalink.framework.json.utils.JsonUtil;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.File;
import java.lang.reflect.Method;

@Provider
public class ControllerReturnValueHandler implements ContainerResponseFilter {

    // 注入 JAX-RS 标准的 ResourceInfo，获取当前请求的方法和类信息
    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        // 1. 判断是否需要跳过包装
        if (isSkip(responseContext)) {
            return;
        }

        // 2. 无注解则执行统一包装
        responseContext.setEntity(JsonUtil.toJsonString(ResultWrapper.success(responseContext.getEntity())));
    }

    /**
     * 判断是否需要跳过包装
     */
    private boolean isSkip(ContainerResponseContext responseContext) {
        // 获取当前请求的方法
        Method method = resourceInfo.getResourceMethod();
        // 检查方法是否标注了 @IgnoredResultWrapper
        if (method != null && method.isAnnotationPresent(IgnoredResultWrapper.class)) {
            return true;
        }

        // 获取当前请求的类
        Class<?> resourceClass = resourceInfo.getResourceClass();
        // 检查类是否标注了 @IgnoredResultWrapper
        if (resourceClass != null && resourceClass.isAnnotationPresent(IgnoredResultWrapper.class)) {
            return true;
        }

        // 获取响应实体
        Object entity = responseContext.getEntity();
        // 如果实体是 File 类型，则跳过包装
        if (entity instanceof File) {
            return true;
        }
        // 如果实体是 ResultWrapper 类型，则跳过包装
        return entity instanceof ResultWrapper;
    }
}
