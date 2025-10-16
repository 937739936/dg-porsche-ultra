package com.shdatalink.sip.server.gb28181.core.builder;

import com.shdatalink.sip.server.gb28181.core.bean.model.base.Message;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SipPublisherBuilder {
    private final String subscribeKey;

    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(200);

    public SipPublisherBuilder(String key, long expired) {
        this.subscribeKey = key;
        CompletableFuture<Message<?>> resultFuture = completeOnTimeout(key, expired, TimeUnit.SECONDS);
        SipPublisher.pendingRequestMap.put(key, resultFuture);
    }

    public <T> FutureEvent<T> build() {
        final CompletableFuture<Message<?>> messageCompletableFuture = SipPublisher.pendingRequestMap.get(this.subscribeKey);
        return new FutureEvent<>(messageCompletableFuture);
    }


    private CompletableFuture<Message<?>> completeOnTimeout(String key, long timeout, TimeUnit unit) {
        final CompletableFuture<Message<?>> promise = new CompletableFuture<>();
        executorService.schedule(() -> {
            if (!promise.isDone()) {
                Message<?> message = new Message<>(key).handlerTimeOut();
                promise.complete(message);
            }
        }, timeout, unit);
        return promise;
    }

}
