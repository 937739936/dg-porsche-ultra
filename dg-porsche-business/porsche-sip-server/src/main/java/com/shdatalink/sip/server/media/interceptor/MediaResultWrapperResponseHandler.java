package com.shdatalink.sip.server.media.interceptor;

import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.json.utils.JsonUtil;
import com.shdatalink.sip.server.media.bean.entity.resp.MediaServerResponse;
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
public class MediaResultWrapperResponseHandler implements ClientResponseFilter {
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
            MediaServerResponse<?> apiResponse = JsonUtil.parseObject(originalBody, MediaServerResponse.class);

            // 4. 验证业务状态码（假设200为成功）
            assert apiResponse != null;
            if (apiResponse.getCode() != 0) {
                // 非成功状态：抛出异常（会被客户端捕获）
                throw new BizException("500", "media server报错: " + apiResponse.getMsg() + " (code: " + apiResponse.getCode() + ")");
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
