package com.shdatalink.resource;

import com.shdatalink.entity.Contract;
import com.shdatalink.framework.redis.annotation.RepeatSubmit;
import com.shdatalink.framework.redis.utils.RedisUtil;
import com.shdatalink.service.CacheService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Path("/redis")
public class RedisResource {

    @Inject
    RedisUtil redisUtil;
    @Inject
    CacheService cacheService;

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

    @GET
    @Path("/list/set")
    public Boolean listSet() {
        List<Contract> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Contract contract = new Contract();
            list.add(contract);
        }
        redisUtil.setList("demoList", list);
        return true;
    }

    @GET
    @Path("/list/get")
    public List<Contract> listGet() {
        return redisUtil.getList("demoList", Contract.class);
    }

    @GET
    @Path("/map/set")
    public Boolean mapSet() {
        Map<String,Contract> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            Contract contract = new Contract();
            map.put(String.valueOf(i),contract);
        }
        redisUtil.setMap("demoMap", map);
        return true;
    }

    @GET
    @Path("/map/get")
    public Contract mapGet(@QueryParam("key") Integer key) {
        return redisUtil.getMapValue("demoMap", key.toString(), Contract.class);
    }


    @RepeatSubmit(interval = 60, timeUnit = TimeUnit.SECONDS)
    @POST
    @Path("/repeatSubmit")
    public boolean repeatSubmit(@QueryParam("key") Integer key, @Valid RepeatSubmitParam param) {
        try {
            TimeUnit.SECONDS.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Data
    public static class RepeatSubmitParam {
        private String name;
        private String password;
    }


}
