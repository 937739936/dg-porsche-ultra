package com.shdatalink.sip.server.media.interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JavaType;
import com.shdatalink.framework.json.utils.JsonUtil;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

@Provider
public class GenericMediaResponseInterceptor implements ReaderInterceptor {

    private static final ObjectMapper MAPPER = JsonUtil.getObjectMapper();

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context)
            throws IOException, WebApplicationException {

        Type returnType = context.getType();

        // 不是 MediaServerResponse 的直接跳过
        if (!isMediaServerResponse(returnType)) {
            return context.proceed();
        }

        // 读取原始响应体
        InputStream inputStream = context.getInputStream();
        String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        // 解析 JSON 根节点
        JsonNode root = MAPPER.readTree(json);
        JsonNode dataNode = root.get("data");

        if (dataNode == null || dataNode.isNull()) {
            return null;
        }

        // 解析泛型类型 T
        JavaType javaType = getGenericType(context);
        Object dataObj = MAPPER.readValue(
                MAPPER.writeValueAsBytes(dataNode),
                javaType
        );

        return dataObj;
    }

    /** 判断是否为 MediaServerResponse 类型 */
    private boolean isMediaServerResponse(Type type) {
        if (!(type instanceof ParameterizedType)) return false;
        ParameterizedType pt = (ParameterizedType) type;
        return pt.getRawType().getTypeName().endsWith("MediaServerResponse");
    }

    /** 获取泛型 T 的 JavaType */
    private JavaType getGenericType(ReaderInterceptorContext context) {
        Type genericType = context.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            Type tType = pt.getActualTypeArguments()[0];
            return MAPPER.getTypeFactory().constructType(tType);
        } else {
            // 没有泛型则退化成 Object
            return MAPPER.getTypeFactory().constructType(Object.class);
        }
    }
}
