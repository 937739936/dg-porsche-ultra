package com.shdatalink.sip.server.module.alarmplan;

import com.shdatalink.sip.server.module.alarmplan.service.SubscribeService;
import com.shdatalink.sip.server.module.alarmplan.vo.SubscribeReq;
import com.shdatalink.sip.server.module.alarmplan.vo.SubscribeResp;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

/**
 * 订阅管理
 */
@Path("admin/subscribe")
public class SubscribeController {

    @Inject
    SubscribeService subscribeService;

    /**
     * 获取订阅信息
     */
    @Path("info")
    @GET
    public SubscribeResp info(@QueryParam("deviceId") String deviceId){
        return subscribeService.info(deviceId);
    }

    /**
     * 订阅管理
     */
    @Path("save")
    @POST
    public Boolean save(@Valid SubscribeReq subscribeReq){
        subscribeService.save(subscribeReq);
        return true;
    }






}
