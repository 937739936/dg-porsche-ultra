package com.shdatalink.exception;

import com.shdatalink.framework.common.exception.BaseResultCodeEnum;
import com.shdatalink.framework.common.model.ResultWrapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

/**
 * 请求方式错误
 * 如post方式请求get接口
 */
@Slf4j
@Provider
public class NotAllowedExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<NotAllowedException> {


    @Override
    public Response toResponse(NotAllowedException e) {
        log.error("❌接口请求方式错误，uri：{}，异常信息：{}",  uriInfo.getRequestUri().getPath(), e.getMessage());
        ResultWrapper<Void> response = ResultWrapper.fail(BaseResultCodeEnum.METHOD_NOT_ALLOWED, e.getMessage());
        return Response.status(HttpResponseStatus.METHOD_NOT_ALLOWED.code()).entity(response).build();
    }
}
