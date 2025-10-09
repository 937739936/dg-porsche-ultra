package com.shdatalink.exception;

import com.shdatalink.framework.common.exception.BaseResultCodeEnum;
import com.shdatalink.framework.common.model.ResultWrapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.runtime.LaunchMode;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理器，根据不同异常类型返回差异化响应
 * <p>
 * 处理业务异常
 */
@Slf4j
@Provider
public class SystemExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<Exception> {


    @Override
    public Response toResponse(Exception e) {
        // 打印堆栈信息
        String exceptionMsgWithStack = getExceptionMsgWithStack(e);
        log.error("❌系统异常，请排查代码或数据，\uD83D\uDD17uri：{}，异常类型：{},ℹ异常信息：{}", uriInfo.getRequestUri().getPath(),e.getClass().getSimpleName(), exceptionMsgWithStack, e);
        // 生产上为了安全不响应给客户端，其他环境可以返回以提高排查效率
        ResultWrapper<Void> response = ResultWrapper.fail(BaseResultCodeEnum.SYSTEM_ERROR, LaunchMode.PROD_PROFILE.equals(launchMode.getProfileKey()) ? "" : exceptionMsgWithStack);
        return Response.status(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                .entity(response)
                .build();
    }

    /**
     * 分析报错位置，帮助排查问题
     */
    private String getExceptionMsgWithStack(Exception exception) {
        StringBuilder errorMsg = new StringBuilder(exception.toString());
        for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
            // 将堆栈信息中第一个业务代码的位置显示出来
            if (stackTraceElement.toString().contains("com.shdatalink")) {
                errorMsg.append("，⚓异常定位：");
                errorMsg.append(stackTraceElement);
                break;
            }
        }
        return errorMsg.toString();
    }
}
