package com.shdatalink.web.exception;

import com.shdatalink.framework.common.exception.BaseResultCodeEnum;
import com.shdatalink.framework.common.exception.UnAuthorizedException;
import com.shdatalink.framework.common.model.ResultWrapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

/**
 * 未授权异常
 */
@Slf4j
@Provider
public class UnAuthorizedExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<UnAuthorizedException> {


    @Override
    public Response toResponse(UnAuthorizedException e) {
        ResultWrapper<Void> response = ResultWrapper.fail(BaseResultCodeEnum.UNAUTHORIZED_EXCEPTION, e.getMessage());
        return Response.status(HttpResponseStatus.UNAUTHORIZED.code()).entity(response).build();
    }

}
