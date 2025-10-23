package com.shdatalink.event;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

/**
 * 事件发布者
 */
@ApplicationScoped
public class EventPublisher {

    @Inject
    Event<Object> genericEvent;

    /**
     * 触发事件(同步)
     *
     * @param <T> 事件类型
     * @param event 需要触发的事件对象
     */
    public <T> void fire(T event) {
        genericEvent.fire(event);
    }

    /**
     * 异步触发事件(异步)
     *
     * @param <T> 事件类型
     * @param event 要触发的事件对象
     */
    public <T> void fireAsync(T event) {
        genericEvent.fireAsync(event);
    }

}
