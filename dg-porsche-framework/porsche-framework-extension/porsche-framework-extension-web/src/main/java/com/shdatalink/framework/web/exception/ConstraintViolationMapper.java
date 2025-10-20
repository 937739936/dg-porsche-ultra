package com.shdatalink.framework.web.exception;

import com.shdatalink.framework.common.exception.BaseResultCodeEnum;
import com.shdatalink.framework.common.model.ResultWrapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * validate 参数校验错误异常
 * 接口入参@Validated的简单类型校验不通过
 */
@Slf4j
@Provider
public class ConstraintViolationMapper extends BaseExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String errorMsg = violations.stream()
                .map(violation -> ((PathImpl) violation.getPropertyPath()).getLeafNode().getName() + violation.getMessage())
                .collect(Collectors.joining(";"));

        log.error("❌接口参数校验异常，uri：{}，异常信息：{}", uriInfo.getRequestUri().getPath(), errorMsg);
        ResultWrapper<Void> response = ResultWrapper.fail(BaseResultCodeEnum.ILLEGAL_ARGUMENT, errorMsg);
        return Response.status(HttpResponseStatus.BAD_REQUEST.code()).entity(response).build();
    }
}
