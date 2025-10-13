package com.shdatalink.sip.service.module;

import com.shdatalink.utils.IpUtil;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/test")
public class ExampleResource {

    /**
     * 获取客户端IP地址
     */
    @Path("/ip")
    @GET
    public String getId() {
        return IpUtil.getClientIpAddress();
    }


}
