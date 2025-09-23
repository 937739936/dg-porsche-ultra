package org.acme.cdi.dcorator;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.math.BigDecimal;

@Path("/decorator")
public class DecoratorResource {

    @Inject
    AccountServiceServiceImpl accountService;

    @GET
    public String a(){
        accountService.withdraw(new BigDecimal("1000"));
        return "hello";
    }
}
