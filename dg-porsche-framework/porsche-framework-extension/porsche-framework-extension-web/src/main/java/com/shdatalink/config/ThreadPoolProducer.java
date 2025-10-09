package com.shdatalink.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.context.ThreadContext;


/**
 * 自定义线程池生产者，用于异步任务处理。
 */
@ApplicationScoped
public class ThreadPoolProducer {

    @Produces
    @ApplicationScoped
    public ManagedExecutor customExecutor() {
        return ManagedExecutor.builder()
                .maxAsync(200) // 最大并发任务数
                .maxQueued(1000) // 任务队列大小
                .propagated(ThreadContext.CDI, ThreadContext.TRANSACTION) // 传播 CDI 和事务上下文
                .build();
    }
}
