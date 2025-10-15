package com.shdatalink.sip.server.module.device.schedule;

import com.shdatalink.sip.server.gb28181.core.bean.constants.InviteTypeEnum;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import com.shdatalink.sip.server.module.device.event.DeviceOnlineEvent;
import com.shdatalink.sip.server.module.device.service.SseClient;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@ApplicationScoped
public class DeviceSchedule {

    /**
     * 设备在线状态查询任务
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    @RunOnVirtualThread
    public void onlineCheck() {
        log.info("onlineCheck start");
    }

    @Scheduled(cron = "0 0/15 * * * ?")
    @RunOnVirtualThread
    public void snapshot() {
        log.info("snapshot start");
    }

    @Scheduled(cron = "0 0/2 * * * ?")
    @RunOnVirtualThread
    public void snapshotExistsStream() {
        log.info("snapshotExistsStream start");
    }

    /**
     * 刷新订阅
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    @RunOnVirtualThread
    public void updateSubscribeExpires() {
        log.info("updateSubscribeExpires start");
    }
}
