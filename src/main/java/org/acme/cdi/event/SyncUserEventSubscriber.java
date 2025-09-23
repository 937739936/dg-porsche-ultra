package org.acme.cdi.event;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class SyncUserEventSubscriber {

    public void onUserRegistered(@Observes UserRegisteredEvent event) {
        // 处理逻辑1：记录用户注册日志
        String registerTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("[同步订阅者-日志] 用户注册成功：" +
                "用户名=" + event.getUsername() +
                "，邮箱=" + event.getEmail() +
                "，注册时间=" + registerTimeStr);
        System.out.println("同步订阅者执行线程:"+Thread.currentThread().getName());
    }
}
