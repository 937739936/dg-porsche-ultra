package org.acme.cdi.dcorator;


import java.math.BigDecimal;

public interface AccountService {
    void withdraw(BigDecimal amount);
}
