package com.shdatalink.sip.server.media.interceptor;

import com.shdatalink.framework.json.utils.JsonUtil;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;

@Slf4j
public class MediaHttpRequestLogger implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (requestContext.getEntity() != null) {
            log.info("=== 请求数据 ===");
            log.info("url: {}", requestContext.getUri());
            log.info("原始对象: {}", requestContext.getEntity());
            String entityJson = JsonUtil.toJsonString(requestContext.getEntity());
            requestContext.setEntity(entityJson);
            log.info("BODY: {}", entityJson);
        }
    }
}
