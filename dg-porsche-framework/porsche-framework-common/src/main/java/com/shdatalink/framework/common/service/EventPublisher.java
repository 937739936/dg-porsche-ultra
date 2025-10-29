package com.shdatalink.framework.common.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;

import static jakarta.transaction.Status.STATUS_COMMITTED;

/**
 * 事件发布者
 */
@ApplicationScoped
public class EventPublisher {

    @Inject
    Event<Object> genericEvent;

    @Inject
    TransactionSynchronizationRegistry tsr;

    /**
     * 触发事件(同步)
     *
     * @param <T>   事件类型
     * @param event 需要触发的事件对象
     */
    public <T> void fire(T event) {
        genericEvent.fire(event);
    }

    /**
     * 异步触发事件(异步)
     *
     * @param <T>   事件类型
     * @param event 要触发的事件对象
     */
    public <T> void fireAsync(T event) {
        genericEvent.fireAsync(event);
    }

    /**
     * 触发事件(同步)
     * <p>
     * 在事务提交后
     *
     * @param <T>   事件类型
     * @param event 需要触发的事件对象
     */
    public <T> void fireAfterCommit(T event) {
        tsr.registerInterposedSynchronization(new Synchronization() {
            @Override
            public void beforeCompletion() {
            }

            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_COMMITTED) {
                    genericEvent.fire(event);
                }
            }
        });
    }

    /**
     * 异步触发事件(异步)
     * <p>
     * 在事务提交后
     *
     * @param <T>   事件类型
     * @param event 要触发的事件对象
     */
    public <T> void fireAsyncAfterCommit(T event) {
        tsr.registerInterposedSynchronization(new Synchronization() {
            @Override
            public void beforeCompletion() {
            }

            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_COMMITTED) {
                    genericEvent.fireAsync(event);
                }
            }
        });
    }

}
