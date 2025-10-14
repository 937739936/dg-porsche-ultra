// src/main/java/com/example/demo/listener/ContractEventListener.java
package com.shdatalink.event;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.ObservesAsync;
import lombok.extern.slf4j.Slf4j;

/**
 * 同步监听器
 */
@Slf4j
@ApplicationScoped
public class ContractEventListener {


    // 使用 @Observes 注解来监听 ContractEvent 类型的事件
    public void onContractCreated(@Observes ContractEvent event) {
        // 这个监听器会处理所有类型的 ContractEvent
        log.info("同步监听器收到事件: {}", event);

        // 可以根据事件内容执行不同操作
        if ("CREATE".equals(event.getAction())) {
            log.info("执行合同创建后的逻辑 '{}'", event.getContractId());
        } else if ("DELETE".equals(event.getAction())) {
            log.info("执行合同删除后的逻辑 '{}'", event.getContractId());
        }
    }

    // 使用 @ObservesAsync 注解来实现异步监听
    public void onContractEventAsync(@ObservesAsync ContractEvent event) {
        log.info("异步监听器收到事件: {}", event);
        try {
            // 模拟一个耗时操作
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("异步处理 '{}' 事件完成。", event.getAction());
    }
}
