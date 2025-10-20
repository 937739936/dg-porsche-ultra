package com.shdatalink.resource;

import com.shdatalink.entity.Contract;
import com.shdatalink.framework.redis.utils.RedisUtil;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import java.math.BigDecimal;
import java.time.LocalDate;

@Path("/redis")
public class RedisResource {

    @Inject
    RedisUtil redisUtil;

    @GET
    @Path("/set")
    public String set(@QueryParam("key") String key) {
        // 设置带过期时间的键值对
//        redisService.set(key, value);
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

        redisUtil.set(key, contract);
        return "设置成功: " + key;
    }

    @GET
    @RunOnVirtualThread
    @Path("/get")
    public Contract get(@QueryParam("key") String key) {
        // 设置带过期时间的键值对
//        return redisService.get(key);
//        return redisUtil.get(key);
        return redisUtil.get(key, Contract.class);
    }

    @DELETE
    @Path("/delete")
    public Boolean delete(@QueryParam("key") String key) {
        redisUtil.del(key);
        return true;
    }


}
