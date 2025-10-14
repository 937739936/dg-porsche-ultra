package com.shdatalink.web.exception;

import com.shdatalink.framework.common.exception.BaseResultCodeEnum;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.framework.common.model.ResultWrapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理请求资源不存在异常，NotFoundException
 */
@Slf4j
@Provider
public class NotFoundExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<NotFoundException> {


    @Override
    public Response toResponse(NotFoundException e) {
        log.info("请求资源不存在，uri：{}，异常信息：{}", uriInfo.getRequestUri().getPath(), e.getMessage());
        ResultWrapper<Void> response = ResultWrapper.fail(BaseResultCodeEnum.RESOURCE_NOT_FOUND, BaseResultCodeEnum.RESOURCE_NOT_FOUND.getMessage());
        return Response.status(HttpResponseStatus.NOT_FOUND.code()).entity(response).build();
    }
}
