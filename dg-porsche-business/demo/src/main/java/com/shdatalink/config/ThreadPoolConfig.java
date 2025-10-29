package com.shdatalink.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.Produces;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class ThreadPoolConfig {


    @Produces
    @ApplicationScoped
    @Named("app-executor")
    public ExecutorService createAppExecutor() {
        return new ThreadPoolExecutor(
                5, // corePoolSize（核心线程数）
                10, // maximumPoolSize（最大线程数）
                60, // keepAliveTime（非核心线程空闲存活时间，单位：秒）
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100), // workQueue（任务队列，容量100）
                new ThreadFactory() {
                    private final AtomicInteger counter = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("app-thread-" + counter.incrementAndGet());
                        thread.setPriority(Thread.NORM_PRIORITY);
                        return thread;
                    }
                }
        );
    }
}
