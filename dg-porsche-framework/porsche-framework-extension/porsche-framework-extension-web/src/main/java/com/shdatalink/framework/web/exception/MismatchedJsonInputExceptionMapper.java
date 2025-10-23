package com.shdatalink.framework.web.exception;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.shdatalink.framework.common.exception.BaseResultCodeEnum;
import com.shdatalink.framework.common.model.ResultWrapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

/**
 * JSON 反序列化异常
 */
@Slf4j
@Provider
public class MismatchedJsonInputExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<MismatchedInputException> {

    @Override
    public Response toResponse(MismatchedInputException e) {
        log.info("请求数据解析失败，uri：{}，异常信息：{}", uriInfo.getRequestUri().getPath(), e.getTargetType());
        ResultWrapper<Void> response = ResultWrapper.fail(BaseResultCodeEnum.ILLEGAL_ARGUMENT, e.getOriginalMessage());
        return Response.status(HttpResponseStatus.BAD_REQUEST.code()).entity(response).build();
    }
}
