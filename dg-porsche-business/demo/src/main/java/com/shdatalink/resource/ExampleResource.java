package com.shdatalink.resource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.shdatalink.entity.Contract;
import com.shdatalink.framework.common.annotation.IgnoredResultWrapper;
import com.shdatalink.framework.common.exception.BizException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Path("/hello")
public class ExampleResource {

    @Path("/testJsonException")
    @POST
    public DemoReq testJsonException(@Valid DemoReq demoReq) {
        return demoReq;
    }


    @Path("/testThrowException")
    @GET
    public Integer testThrowException(@QueryParam("num") @NotNull Integer num, @QueryParam("num2") @Max(10) Integer num2) {
        Integer num1 = 10;
        if (num < 0) {
            throw new BizException("num不能小于零");
        }
        num1 = num1 / num;
        return num1;
    }


    @IgnoredResultWrapper
    @GET
    public Contract hello() {
        Contract contract = new Contract();
        contract.setId(0L);
        contract.setContractNo("");
        contract.setContractType("");
        contract.setFirstCategoryId(0L);
        contract.setSecondCategoryId(0L);
        contract.setStatus("");
        contract.setBuyerUscc("");
        contract.setBuyerName("");
        contract.setSellerUscc("");
        contract.setSellerName("");
        contract.setSignDate(LocalDate.now());
        contract.setUnitPrice(new BigDecimal("0"));
        contract.setQuantity(new BigDecimal("0"));
        contract.setStartDate(LocalDate.now());
        contract.setEndDate(LocalDate.now());

        return contract;
    }

    @Data
    public static class DemoReq {
        @NotBlank
        @Length(max = 10)
        private String name;
        @NotNull
        @Min(0)
        @Max(100)
        private Integer age;
    }

}
