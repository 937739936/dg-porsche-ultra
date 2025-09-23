package org.acme.cdi.event;

import java.time.LocalDateTime;

public class UserRegisteredEvent {
    // 事件关联的数据字段
    private final String username;
    private final String email;
    private final LocalDateTime registerTime; // 注册时间戳

    // 构造函数：初始化事件数据（建议用构造器注入，保证不可变性）
    public UserRegisteredEvent(String username, String email, LocalDateTime registerTime) {
        this.username = username;
        this.email = email;
        this.registerTime = registerTime;
    }

    // Getter 方法：提供只读访问（避免事件数据被篡改）
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getRegisterTime() {
        return registerTime;
    }
}
