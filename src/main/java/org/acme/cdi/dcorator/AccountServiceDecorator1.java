package org.acme.cdi.dcorator;

import jakarta.annotation.Priority;
import jakarta.decorator.Decorator;
import jakarta.decorator.Delegate;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Decorated;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.inject.Inject;

import java.math.BigDecimal;

@Priority(1)
@Decorator
public class AccountServiceDecorator1 implements AccountService {

    /**
     * @Delegate 注入的并不是“真实的原始对象实例”，而是容器代理（proxy） 它代表的是被装饰的“下一个对象”，从而形成一个装饰链。
     *
     * Client 调用 AccountService →
     *    Proxy (delegate 代理) →
     *       AccountServiceDecorator1 →
     *          AccountServiceDecorator2 →
     *             AccountServiceServiceImpl
     *
     * 多个装饰器时，形成一个责任链/装饰链。执行顺序按照@Priority取值来决定， 每一层的 @Delegate 注入的其实是“下一个代理”，直到最终落到真实的 GreetingServiceImpl。
     */
    @Inject
    @Any
    @Delegate
    AccountService decorator;


    @Inject
    @Decorated
    Bean<AccountService> delegateInfo;


    public void withdraw(BigDecimal amount) {
        System.out.println("concreteObject: " + delegateInfo.getBeanClass());
        System.out.println("before:装饰器1执行了....");
        decorator.withdraw(amount);
        System.out.println("after:装饰器1执行了....");
    }
}
