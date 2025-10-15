package com.shdatalink.sip.server.module.device.schedule;

import com.shdatalink.sip.server.module.device.service.SseClient;
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
//    @Scheduled(cron = "*/10 * * * * ?")
//    @RunOnVirtualThread
    void test() {
        log.info("test");
        sseClient.sendDeviceMessage(LocalDateTime.now());
    }

//    @Scheduled(cron = "*/10 * * * * ?")
//    @RunOnVirtualThread
//    void test2() {
//        log.info("test2");
//        sseClient.sendDeviceMessage(LocalDateTime.now());
//    }
}
