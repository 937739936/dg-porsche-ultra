package com.shdatalink.sip.server.media.interceptor;

import com.shdatalink.json.utils.JsonUtil;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class MediaHttpRequestLogger implements ClientRequestFilter {
    
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (requestContext.getEntity() != null) {
            String entityJson = JsonUtil.toJsonString(requestContext.getEntity());
            log.info("=== 请求数据 ===");
            log.info("原始对象: {}", requestContext.getEntity());
            log.info("BODY: {}", entityJson);
            requestContext.setEntity(entityJson);
        }
    }
}