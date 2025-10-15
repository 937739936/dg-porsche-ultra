package com.shdatalink.sip.server.module;

import com.shdatalink.framework.common.annotation.CheckPermission;
import com.shdatalink.framework.common.enums.CheckPermissionMode;
import com.shdatalink.sip.server.common.DictEnumCollector;
import com.shdatalink.sip.server.module.user.convert.UserConvert;
import com.shdatalink.sip.server.module.user.entity.User;
import com.shdatalink.sip.server.module.user.vo.UserInfo;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.util.List;

/**
 * 测试
 */
//@Path("admin/test")
@Path("test")
public class ExampleResource {

    @Inject
    UserConvert userConvert;


    @Path("/mapstruct")
    @GET
    public UserInfo mapstruct() {
        User user = new User();
        user.setUsername("test");
        return userConvert.toUserInfo(user);
    }

    /**
     * 权限校验注解
     */
    @CheckPermission(value = {"device:add", "test1"}, mode = CheckPermissionMode.AND)
//    @CheckPermission(value = {"device:add", "test1"}, mode = CheckPermissionMode.OR)
    @Path("/checkPermission")
    @GET
    public Boolean checkPermission() {
        return true;
    }


}
