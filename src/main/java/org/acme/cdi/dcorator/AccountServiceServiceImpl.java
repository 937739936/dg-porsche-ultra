package org.acme.cdi.dcorator;

import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;

@ApplicationScoped
public class AccountServiceServiceImpl implements AccountService {
    public void withdraw(BigDecimal amount) {
        System.out.println("accountServiceImpl.withdraw....");
    }
}
