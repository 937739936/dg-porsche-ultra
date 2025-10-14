package com.shdatalink.web.exception;

import io.quarkus.runtime.LaunchMode;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

/**
 * 全局异常捕获以及错误日志管理
 * <p>
 * - 异常可以分为业务异常、程序异常，程序异常又分为前端传参异常和后端程序异常
 * - 只有程序异常需要打印error日志，配合日志采集实时跟踪程序异常
 * - 同时如果前端传参异常需要给出400响应码且明确提示错误原因，方便甩锅
 */
@Provider
public class BaseExceptionMapper {

    @Context
    UriInfo uriInfo;

    @Inject
    LaunchMode launchMode;
}
