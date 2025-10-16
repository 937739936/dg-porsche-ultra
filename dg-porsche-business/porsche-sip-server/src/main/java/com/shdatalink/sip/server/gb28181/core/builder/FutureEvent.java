package com.shdatalink.sip.server.gb28181.core.builder;

import com.shdatalink.sip.server.gb28181.core.bean.model.base.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class FutureEvent<T> {
    private final CompletableFuture<Message<?>> future;


    public FutureEvent(CompletableFuture<Message<?>> future) {
        this.future = future;
    }

    public Message<T> get() {
        try {
            Message<?> message = future.get();
            Message.EnumState code = message.getCode();
            if (code == Message.EnumState.OK) {
                return (Message<T>) message;
            }
            throw new RuntimeException(message.getMsg());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}