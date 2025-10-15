package com.shdatalink.sip.service.module.device.schedule;

import com.shdatalink.sip.service.module.device.service.SseClient;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;


@Slf4j
@ApplicationScoped
public class DeviceSchedule {

    @Inject
    SseClient sseClient;

//    @Scheduled(cron = "0 */10 * * * *")
    @Scheduled(cron = "*/10 * * * * ?")
    @RunOnVirtualThread
    void test() {
        sseClient.sendDeviceMessage(LocalDateTime.now());
    }
}
