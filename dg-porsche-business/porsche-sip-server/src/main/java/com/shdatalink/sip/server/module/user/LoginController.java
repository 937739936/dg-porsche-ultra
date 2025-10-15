package com.shdatalink.sip.server.module.user;

import com.shdatalink.framework.common.annotation.Anonymous;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.framework.common.utils.PasswordUtil;
import com.shdatalink.sip.server.module.common.enums.OperateLogTypeEnum;
import com.shdatalink.sip.server.module.common.service.OperateLogService;
import com.shdatalink.sip.server.module.user.entity.User;
import com.shdatalink.sip.server.module.user.service.LoginService;
import com.shdatalink.sip.server.module.user.service.UserService;
import com.shdatalink.sip.server.module.user.vo.LoginRequest;
import com.shdatalink.sip.server.module.user.vo.TokenResp;
import com.shdatalink.sip.server.module.user.vo.UserInfo;
import com.shdatalink.sip.server.utils.UserInfoUtil;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录相关接口
 */
@Path("admin/login")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    @Inject
    LoginService loginService;
    @Inject
    UserService userService;
    @Inject
    OperateLogService operateLogService;


    /**
     * 账号密码登录
     */
    @POST
    @Path("account")
    @Anonymous
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

    /**
     * 退出登录
     */
    @GET
    @Path(value = "/logout")
    public Boolean logout(@NotBlank @HeaderParam("authorization") String token) {
        UserInfo userInfo = UserInfoUtil.getUserInfoWithThrow();
        if (userInfo != null) {
            operateLogService.addLog("用户名: " + userInfo.getFullName() + ",退出成功！", OperateLogTypeEnum.LOGIN, userInfo.getId(), userInfo.getFullName());
            log.info(" 用户名:  " + userInfo.getFullName() + ",退出成功！ ");
            return true;
        } else {
            throw new BizException("无效的令牌");
        }
    }

}
