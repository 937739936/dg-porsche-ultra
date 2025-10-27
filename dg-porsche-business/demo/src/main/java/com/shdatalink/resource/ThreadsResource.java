package com.shdatalink.resource;

import io.quarkus.virtual.threads.VirtualThreads;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;

@Slf4j
@Path("/thread")
public class ThreadsResource {

    // 注入虚拟线程执行器
    @Inject
    @VirtualThreads
    ExecutorService vThreads;


    @Path("/execute")
    @GET
    public LocalDateTime execute() {
        vThreads.execute(() -> {
            log.info("Hello from a virtual thread!");
        });
        return LocalDateTime.now();
    }




}
