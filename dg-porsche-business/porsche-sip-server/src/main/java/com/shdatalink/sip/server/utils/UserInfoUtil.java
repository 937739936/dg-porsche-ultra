package com.shdatalink.sip.server.utils;


import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.sip.server.config.web.UserInfoThreadHolder;
import com.shdatalink.sip.server.module.user.vo.UserInfo;

import java.util.List;
import java.util.Optional;

public class UserInfoUtil {

    public static UserInfo getUserInfoWithThrow(UserInfo userInfo) {
        return Optional.ofNullable(userInfo)
                .orElseThrow(() -> new BizException("用户登陆信息为空"));
    }

    public static UserInfo getUserInfoWithThrow() {
        return Optional.ofNullable(UserInfoThreadHolder.getCurrentUser())
                .orElseThrow(() -> new BizException("未找到当前登录人信息"));
    }

    public static UserInfo getUserInfoWithNull() {
        return UserInfoThreadHolder.getCurrentUser();
    }

    public static List<String> getDeviceIdList() {
        return getUserInfoWithThrow().getDeviceIds();
    }

    public static Integer getUserId() {
        return getUserInfoWithThrow().getId();
    }
}
