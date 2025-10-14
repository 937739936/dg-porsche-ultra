package com.shdatalink.sip.service.module.user.service;

import com.shdatalink.json.utils.JsonUtil;
import com.shdatalink.jwt.utils.JwtUtil;
import com.shdatalink.sip.service.common.constants.RedisKeyConstants;
import com.shdatalink.sip.service.module.user.convert.UserConvert;
import com.shdatalink.sip.service.module.user.entity.Role;
import com.shdatalink.sip.service.module.user.entity.User;
import com.shdatalink.sip.service.module.user.entity.Permission;
import com.shdatalink.sip.service.module.user.mapper.UserMapper;
import com.shdatalink.sip.service.module.user.vo.TokenResp;
import com.shdatalink.sip.service.module.user.vo.UserInfo;
import com.shdatalink.redis.utils.RedisUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@ApplicationScoped
public class LoginService {

    @Inject
    RoleService roleService;
    @Inject
    RedisUtil redisUtil;
    @Inject
    UserMapper userMapper;
    @Inject
    UserConvert userConvert;

    /**
     * 账号登录
     *
     * @return 用户信息
     */
    @Transactional(rollbackOn = Exception.class)
    public TokenResp accountLogin(User user) {
        //用户登录信息
        String sysPassword = user.getPassword();
        TokenResp tokenResp = new TokenResp();
        UserInfo userInfo = userConvert.toUserInfo(user);

        // 生成token
        String token = JwtUtil.sign(JsonUtil.toJsonString(userInfo), sysPassword);
        // 如果是管理员，则赋予所有权限
        List<Role> role = roleService.getRoleByUserId(user.getId());
        List<String> permissionList;
        if ("admin".equals(user.getUsername())) {
            permissionList = roleService.getAllPermissions();
        } else {
            permissionList = roleService.getPermissions(role.stream().map(Role::getId).toList())
                    .stream().map(Permission::getPermission)
                    .toList();
        }
        userInfo.setPermissionTokens(permissionList);
        userInfo.setDeviceIds(roleService.getPermissionDevice(role.stream().map(Role::getId).toList()));
        tokenResp.setUserInfo(userInfo);
        userInfo.setRoleIds(role.stream().map(Role::getId).toList());
        tokenResp.setToken(token);
        redisUtil.setEx(RedisKeyConstants.USER_LOGIN_INFO + user.getId(), userInfo, Duration.ofMillis(JwtUtil.EXPIRE_TIME));
        return tokenResp;
    }

    public void refreshUserInfo(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return;
        }
        UserInfo userInfo = userConvert.toUserInfo(user);
        List<Role> role = roleService.getRoleByUserId(userId);
        userInfo.setRoleIds(role.stream().map(Role::getId).toList());
        List<String> list = roleService.getPermissions(role.stream().map(Role::getId).toList())
                .stream().map(Permission::getPermission)
                .toList();
        userInfo.setPermissionTokens(list);
        userInfo.setDeviceIds(roleService.getPermissionDevice(role.stream().map(Role::getId).toList()));
        redisUtil.setEx(RedisKeyConstants.USER_LOGIN_INFO + user.getId(), userInfo, Duration.ofMillis(JwtUtil.EXPIRE_TIME));

    }

    public void removeUserInfo(Integer userId) {
        redisUtil.del(RedisKeyConstants.USER_LOGIN_INFO + userId);
    }

    public UserInfo userInfoByToken(String token) {
        String userInfo = JwtUtil.getValueByKey(token, "userInfo");
        return JsonUtil.parseObject(userInfo, UserInfo.class);
    }
}
