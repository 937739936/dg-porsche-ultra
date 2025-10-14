package com.shdatalink.sip.service.module;

import com.shdatalink.sip.service.module.user.convert.UserConvert;
import com.shdatalink.sip.service.module.user.entity.User;
import com.shdatalink.sip.service.module.user.vo.UserInfo;
import com.shdatalink.web.utils.IpUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/test")
public class ExampleResource {

    @Inject
    UserConvert userConvert;

    /**
     * 获取客户端IP地址
     */
    @Path("/ip")
    @GET
    public String getId() {
        return IpUtil.getIpAddr();
    }

    @Path("/mapstruct")
    @GET
    public UserInfo mapstruct() {
        User user = new User();
        user.setUsername("test");
        return userConvert.toUserInfo(user);
    }


}
