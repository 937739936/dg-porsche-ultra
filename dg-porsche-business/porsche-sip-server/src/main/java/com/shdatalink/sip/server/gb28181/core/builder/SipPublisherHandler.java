package com.shdatalink.sip.server.gb28181.core.builder;

import com.shdatalink.sip.server.gb28181.core.bean.model.base.Message;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.MessageStating;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class SipPublisherHandler {
    private final String key;
    private final CompletableFuture<Message<?>> future;

    public SipPublisherHandler(String key) {
        this.key = key;
        CompletableFuture<Message<?>> future = SipPublisher.pendingRequestMap.get(key);
        if (Objects.isNull(future)) {
            this.future = new CompletableFuture<>();
            Message<Object> message = new Message<>(key).msg("未获取到处理程序").handlerError();
            this.future.complete(message);
        } else {
            this.future = future;
        }
    }

    public <T extends MessageStating<T>> T staging(T data) {
        synchronized (future) {
            T v = MessageStaging.get(key);
            if (v == null) {
                MessageStaging.put(key, data);
                v = data;
            } else {
                v.append(data);
            }
            return v;
        }
    }

    public <T> void ofOk(T data) {
        Message<T> message = new Message<T>(this.key).data(data).handlerOk();
        future.complete(message);
        SipPublisher.unSubscribe(this.key);
    }

    public <T> void ofThen(T data) {
        Message<T> message = new Message<T>(this.key).data(data).handlerOk();
        future.complete(message);
        SipPublisher.unSubscribe(this.key);
    }

    public void ofFail(String msg) {
        Message<Object> message = new Message<>(this.key).msg(msg).handlerError();
        log.info("出错了：{}", msg);
        future.complete(message);
        SipPublisher.unSubscribe(this.key);
    }

    public void ofTimeOut(String msg) {
        Message<Object> message = new Message<>(this.key).msg(msg).handlerTimeOut();
        future.complete(message);
        SipPublisher.unSubscribe(this.key);
    }
}
