package com.shdatalink.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shdatalink.framework.common.annotation.IgnoredResultWrapper;
import com.shdatalink.framework.common.model.ResultWrapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;


import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Controller返回值统一处理
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ControllerReturnValueHandler implements MessageBodyWriter<Object> {

    @Inject
    ObjectMapper objectMapper;


    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        if (annotations != null) {
            // 检查是否有忽略包装的注解
            for (Annotation annotation : annotations) {
                if (annotation instanceof IgnoredResultWrapper) {
                    return false;
                }
            }
        }
        // 已经是ResultWrapper不处理
        return !(ResultWrapper.class.isAssignableFrom(aClass));
    }

    @Override
    public void writeTo(Object o, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        // 包装返回结果
        ResultWrapper<Object> wrappedResult = ResultWrapper.success(o);

        // 序列化为JSON并写入响应流
        objectMapper.writeValue(outputStream, wrappedResult);
    }
}
