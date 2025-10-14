package com.shdatalink.sip.service.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

@ApplicationScoped
public class SipMessageExecutor {

    /**
     * 使用虚拟线程
     */
    @Produces
    @ApplicationScoped
    public ExecutorService createSipMessageExecutor() {
        // 创建虚拟线程工厂
        ThreadFactory virtualThreadFactory = Thread.ofVirtual()
                .name("sip-thread-", 0)
                .factory();

        // 创建承载虚拟线程的平台线程池
        ThreadPoolExecutor platformThreadPool = (ThreadPoolExecutor) Executors.newThreadPerTaskExecutor(virtualThreadFactory);

        // 配置平台线程池参数
        platformThreadPool.setCorePoolSize(100);
        platformThreadPool.setMaximumPoolSize(100);
        return platformThreadPool;
    }
}
