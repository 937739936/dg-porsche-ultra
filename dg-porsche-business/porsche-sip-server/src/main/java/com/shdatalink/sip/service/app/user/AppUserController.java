package com.shdatalink.sip.service.app.user;

import com.shdatalink.framework.common.annotation.Anonymous;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.framework.common.utils.PasswordUtil;
import com.shdatalink.sip.service.module.common.enums.OperateLogTypeEnum;
import com.shdatalink.sip.service.module.common.service.OperateLogService;
import com.shdatalink.sip.service.module.user.entity.User;
import com.shdatalink.sip.service.module.user.service.LoginService;
import com.shdatalink.sip.service.module.user.service.UserService;
import com.shdatalink.sip.service.module.user.vo.LoginRequest;
import com.shdatalink.sip.service.module.user.vo.TokenResp;
import com.shdatalink.sip.service.module.user.vo.UserInfo;
import com.shdatalink.sip.service.utils.UserInfoUtil;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

/**
 * APP/用户管理
 */
@Path("app/user")
public class AppUserController {

    @Inject
    UserService userService;
    @Inject
    LoginService loginService;
    @Inject
    OperateLogService operateLogService;

    /**
     * 获取用户信息
     */
    @GET
    @Path("detail")
    public UserInfo detail() {
        return UserInfoUtil.getUserInfoWithThrow();
    }


    /**
     * 账号密码登录
     */
    @Anonymous
    @POST
    @Path("account")
    public TokenResp accountLogin(@Valid LoginRequest request) {
        User user = userService.getUserByUserName(request.getUsername()).orElseThrow(() -> {
            operateLogService.addLog("用户登录失败，用户不存在！", OperateLogTypeEnum.LOGIN, null, null);
            return new BizException("用户名或密码错误");
        });
        if (user.getEnabled() == null || !user.getEnabled()) {
            operateLogService.addLog("用户登录失败，用户名:" + user.getUsername() + "已禁用！", OperateLogTypeEnum.LOGIN, user.getId(), user.getFullName());
            throw new BizException("用户名或密码错误");
        }
        String userPassword = PasswordUtil.encrypt(request.getUsername(), request.getPassword(), user.getSalt());
        if (!user.getPassword().equals(userPassword)) {
            throw new BizException("用户名或密码错误");
        }
        // 校验账号密码
        return loginService.accountLogin(user);
    }

}
