package org.acme.cdi.dcorator;

import jakarta.annotation.Priority;
import jakarta.decorator.Decorator;
import jakarta.decorator.Delegate;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Decorated;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.inject.Inject;

import java.math.BigDecimal;

@Priority(2)
@Decorator
public class AccountServiceDecorator2 implements AccountService {

    @Inject
    @Any
    @Delegate
    AccountService decorator;


    @Inject
    @Decorated
    Bean<AccountService> delegateInfo;


    public void withdraw(BigDecimal amount) {
        System.out.println("before:装饰器2执行了....");
        decorator.withdraw(amount);
        System.out.println("after:装饰器2执行了....");
    }
}
