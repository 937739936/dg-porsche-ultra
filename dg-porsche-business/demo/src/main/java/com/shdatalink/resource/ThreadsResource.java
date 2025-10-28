package com.shdatalink.resource;

import io.quarkus.virtual.threads.VirtualThreads;
import io.smallrye.context.api.ManagedExecutorConfig;
import io.smallrye.context.api.NamedInstance;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.context.ThreadContext;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;

@Slf4j
@Path("/thread")
public class ThreadsResource {

    // 注入虚拟线程执行器
    @Inject
    @VirtualThreads
    ExecutorService vThreads;

    @Inject
    @Named("app-executor") // 注入自定义线程池
    ExecutorService appExecutor;


    @Path("/execute")
    @GET
    public LocalDateTime execute() {
        vThreads.execute(() -> {
            log.info("Hello from a virtual thread!");
        });
        appExecutor.execute(() -> {
            log.info("Hello from a virtual thread!");
        });
        return LocalDateTime.now();
    }




}
