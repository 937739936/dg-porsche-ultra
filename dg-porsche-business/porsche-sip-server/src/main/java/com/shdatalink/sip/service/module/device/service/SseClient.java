package com.shdatalink.sip.service.module.device.service;

import io.quarkus.runtime.ShutdownEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ApplicationScoped
public class SseClient {

    @Inject
    Sse sse;

    // uuid -> sse
    private static final Map<String, SseEventSink> DEVICE_SSE_MAP = new ConcurrentHashMap<>();

    /**
     * sse订阅
     */
    public void createSse(SseEventSink sink, String uuid) {
        DEVICE_SSE_MAP.put(uuid, sink);
        try {
            // 创建SSE事件
            Map<String, Object> msg = new HashMap<>();
            msg.put("success", true);

            var event = sse.newEventBuilder()
                    .id(uuid)
                    .name("OK")
                    .data(msg)
                    .build();

            // 发送事件（需检查连接是否仍活跃）
            if (!sink.isClosed()) {
                sink.send(event);
            }
        } catch (Exception e) {
            log.error("createSse 失败", e);
            // 关闭连接
            sink.close();
        }
    }


    public void destroy(@Observes ShutdownEvent event) {
        log.info("关闭所有SSE连接");
        for (SseEventSink sink : DEVICE_SSE_MAP.values()) {
            if (sink != null && !sink.isClosed()) {
                sink.close();
            }
        }
    }


    public void sendDeviceMessage(Object message) {
        for (Map.Entry<String, SseEventSink> entry : DEVICE_SSE_MAP.entrySet()) {
            try {
                var event = sse.newEventBuilder()
                        .id(entry.getKey())
                        .name("testEvent")
                        .data(message)
                        .build();

                entry.getValue().send(event);
            } catch (Exception e) {
                log.error("send device message error:{}, {}", entry.getKey(), e.getMessage());
            }
        }
    }

}
