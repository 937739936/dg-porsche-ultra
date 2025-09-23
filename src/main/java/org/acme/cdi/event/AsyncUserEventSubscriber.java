package org.acme.cdi.event;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;

@ApplicationScoped
public class AsyncUserEventSubscriber {


    /**
     * 若事件处理逻辑耗时（如发送邮件、调用第三方接口），
     * 可通过 @ObservesAsync 注解实现异步执行（Quarkus 会自动分配线程池处理，不阻塞事件发布线程）：
     */
    public void sendRegisterEmail(@ObservesAsync UserRegisteredEvent event) {

        try {
            Thread.sleep(2000);

            System.out.println("[异步订阅者-邮件] 向 " + event.getEmail() +
                    " 发送注册确认邮件，用户名：" + event.getUsername());
            System.out.println("异步订阅者执行线程:"+Thread.currentThread().getName());


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
