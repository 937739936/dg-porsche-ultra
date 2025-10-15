package com.shdatalink.sip.server.module.device;

import com.shdatalink.sip.server.module.device.service.SseClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.SseEventSink;

import java.io.IOException;
import java.util.UUID;

/**
 * 设备管理
 */
@Path("admin/device")
public class DeviceController {

    @Inject
    SseClient sseClient;

    /**
     * sse订阅
     * <p>订阅不同事件时，返回的数据结构不同：</p>
     * <p>Online 类型: {@link DeviceSSEResponse.Online}</p>
     * <p>Log 类型: {@link DeviceSSEResponse.MessageLog}</p>
     * <p>RegisterInfoUpdate 类型: {@link DevicePage}</p>
     */
    @GET
    @Path("sse")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void createSseEmitter(@Context SseEventSink eventSink) {
        sseClient.createSse(eventSink, UUID.randomUUID().toString());
    }


}
