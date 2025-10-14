package com.shdatalink.web.exception;

import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.framework.common.model.ResultWrapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理业务异常，BizException
 */
@Slf4j
@Provider
public class BizExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<BizException> {


    @Override
    public Response toResponse(BizException e) {
        log.error("❌业务异常，uri：{}，异常信息：{}", uriInfo.getRequestUri().getPath(), e.getMsg());
        ResultWrapper<Object> response = ResultWrapper.fail(e.getCode(), e.getMsg());
        return Response.status(HttpResponseStatus.CONFLICT.code()).entity(response).build();
    }
}
