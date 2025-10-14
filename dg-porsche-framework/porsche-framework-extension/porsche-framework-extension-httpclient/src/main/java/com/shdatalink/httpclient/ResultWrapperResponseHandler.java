package com.shdatalink.httpclient;

import com.shdatalink.framework.common.exception.BaseResultCodeEnum;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.framework.common.model.ResultWrapper;
import com.shdatalink.json.utils.JsonUtil;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 自定义响应处理类，用于处理特定类型的响应。
 * ResultWrapper 统一返回处理
 */
public class ResultWrapperResponseHandler implements ClientResponseFilter {
    @Override
    public void filter(ClientRequestContext request, ClientResponseContext response) throws IOException {
        // 1. 获取原始响应体输入流
        InputStream originalStream = response.getEntityStream();
        if (originalStream == null) {
            return; // 无响应体时直接返回
        }

        // 2. 将原始响应体转换为字符串
        byte[] bytes = originalStream.readAllBytes();
        String originalBody = new String(bytes);

        try {
            // 3. 解析为ResultWrapper对象
            ResultWrapper<?> apiResponse = JsonUtil.parseObject(originalBody, ResultWrapper.class);

            // 4. 验证业务状态码（假设200为成功）
            if (BaseResultCodeEnum.SUCCESS.name().equals(apiResponse.getCode())) {
                // 非成功状态：抛出异常（会被客户端捕获）
                throw new BizException(apiResponse.getCode(), "业务错误: " + apiResponse.getMessage() + " (code: " + apiResponse.getCode() + ")");
            }

            // 5. 提取data字段作为新的响应体
            Object data = apiResponse.getData();
            if (data == null) {
                // 无数据时返回空流
                response.setEntityStream(new ByteArrayInputStream(new byte[0]));
                return;
            }

            // 6. 将data转换为JSON并设置为新的响应体
            String dataJson = JsonUtil.toJsonString(data);
            response.setEntityStream(new ByteArrayInputStream(dataJson.getBytes()));

        } catch (Exception e) {
            throw new IOException("解析响应失败: " + e.getMessage(), e);
        }
    }
}
