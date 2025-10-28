package com.shdatalink.sip.server.module.device.service;

import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.enums.MessageTypeEnum;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import com.shdatalink.sip.server.module.device.event.ChannelUpdateEvent;
import com.shdatalink.sip.server.module.device.event.DeviceInfoUpdateEvent;
import com.shdatalink.sip.server.module.device.event.DeviceOnlineEvent;
import com.shdatalink.sip.server.module.device.vo.DeviceChannelPage;
import com.shdatalink.sip.server.module.device.vo.DevicePage;
import com.shdatalink.sip.server.module.device.vo.DeviceSSEResponse;
import io.quarkus.runtime.ShutdownEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ApplicationScoped
public class SseClient {

    @Inject
    Sse sse;
    @Inject
    DeviceService deviceService;
    @Inject
    DeviceLogService deviceLogService;

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
                    .mediaType(MediaType.APPLICATION_JSON_TYPE)
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
            DEVICE_SSE_MAP.remove(uuid);
        }
    }


    public void destroy(@ObservesAsync ShutdownEvent event) {
        log.info("关闭所有SSE连接");
        for (SseEventSink sink : DEVICE_SSE_MAP.values()) {
            if (sink != null && !sink.isClosed()) {
                sink.close();
            }
        }
    }

    private void sendDeviceMessage(DeviceSSEResponse.EventType event, Object message) {
        List<String> closed = new ArrayList<>();
        for (Map.Entry<String, SseEventSink> entry : DEVICE_SSE_MAP.entrySet()) {
            try {
                if (!entry.getValue().isClosed()) {
                    entry.getValue().send(
                            sse.newEventBuilder()
                                    .mediaType(MediaType.APPLICATION_JSON_TYPE)
                                    .id(entry.getKey())
                                    .name(event.name())
                                    .data(message)
                                    .build()
                    );
                } else {
                    closed.add(entry.getKey());
                }
            } catch (Exception e) {
                log.error("send device message error:{}, {}", entry.getKey(), e.getMessage());
                closed.add(entry.getKey());
            }
        }
        for (String uuid : closed) {
            if (DEVICE_SSE_MAP.get(uuid).isClosed()) {
                DEVICE_SSE_MAP.remove(uuid);
            }
        }
    }


    public void handleDeviceOnlineEvent(@ObservesAsync DeviceOnlineEvent event) {
        String deviceType = "";
        Device device = deviceService.getByDeviceId(event.getDeviceId()).orElseThrow(() -> new RuntimeException("设备不存在"));
        if (device.getProtocolType() == ProtocolTypeEnum.GB28181) {
            deviceType = "设备";
        }else if (device.getProtocolType() == ProtocolTypeEnum.PULL) {
            deviceType = "拉流";
        }else if (device.getProtocolType() == ProtocolTypeEnum.RTMP) {
            deviceType = "推流";
        }
        // 记录日志
        deviceLogService.addLog(event.getDeviceId(), event.getChannelId(), event.getOnline(), MessageTypeEnum.Online, event.getOnline() ? deviceType + "上线" : deviceType + "离线");

        // 推送sse消息
        DeviceSSEResponse.Online online = new DeviceSSEResponse.Online();
        online.setDeviceId(event.getDeviceId());
        online.setChannelId(event.getChannelId());
        online.setOnline(event.getOnline());
        sendDeviceMessage(DeviceSSEResponse.EventType.Online, online);
    }

    public void handleDeviceInfoUpdateEvent(@ObservesAsync DeviceInfoUpdateEvent event) {
        Device device = event.getDevice();
        DevicePage devicePage = DevicePage.fromDevice(device);
        devicePage.setChannelCount(device.getChannelCount());
        sendDeviceMessage(DeviceSSEResponse.EventType.InfoUpdate, devicePage);
    }

    public void handleChannelUpdate(@ObservesAsync ChannelUpdateEvent event) {
        DeviceChannel channel = event.getChannel();
        sendDeviceMessage(DeviceSSEResponse.EventType.ChannelUpdate, DeviceChannelPage.fromDeviceChannel(channel));
    }
}
