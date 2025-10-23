package com.shdatalink.job;


import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class CustomJob {

    /**
     * cron任务
     */
//    @Scheduled(cron = "*/5 * * * * ?")
    public void cronJob() {
        log.info("cronJob:{}", Thread.currentThread().getName());
    }

    /**
     * 使用虚拟线程执行定时任务
     */
//    @RunOnVirtualThread
//    @Scheduled(every = "10s")
    public void virtualJob() {
        log.info("Scheduled task running on:{}", Thread.currentThread().getName());
    }


}
