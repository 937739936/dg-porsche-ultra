package com.shdatalink.sip.server.module.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.framework.common.utils.PasswordUtil;
import com.shdatalink.json.utils.JsonUtil;
import com.shdatalink.redis.utils.RedisUtil;
import com.shdatalink.sip.server.common.constants.RedisKeyConstants;
import com.shdatalink.sip.server.module.user.convert.UserConvert;
import com.shdatalink.sip.server.module.user.entity.Role;
import com.shdatalink.sip.server.module.user.entity.User;
import com.shdatalink.sip.server.module.user.entity.UserRole;
import com.shdatalink.sip.server.module.user.mapper.UserMapper;
import com.shdatalink.sip.server.module.user.mapper.UserRoleMapper;
import com.shdatalink.sip.server.module.user.vo.*;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.user.service.UserService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@Slf4j
@ApplicationScoped
public class UserService extends ServiceImpl<UserMapper, User> {

    @Inject
    UserRoleMapper userRoleMapper;
    @Inject
    RoleService roleService;
    @Inject
    RedisUtil redisUtil;
    @Inject
    LoginService loginService;
    @Inject
    UserConvert convert;


    public UserInfo getUserInfo(Integer userId) {
        String userInfoStr = redisUtil.get(RedisKeyConstants.USER_LOGIN_INFO + userId);
        if (StringUtils.isBlank(userInfoStr)) {
            return null;
        }
        return JsonUtil.parseObject(userInfoStr, UserInfo.class);
    }

    public UserInfo getUserInfoByUserName(String username) {
        Optional<User> userOptional = getUserByUserName(username);
        return userOptional.map(user -> convert.toUserInfo(user)).orElse(null);
    }

    public Optional<User> getUserByUserName(String username) {
        return this.getOneOpt(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    @Transactional(rollbackOn = Exception.class)
    public void saveUser(UserSaveParam param) {
        User user;
        if (param.getId() == null) {
            user = new User();
        } else {
            user = getOptById(param.getId()).orElseThrow(() -> new BizException("用户不存在"));
        }
        if (param.getId() == null && StringUtils.isBlank(param.getPassword())) {
            throw new BizException("密码不能为空");
        }

        user.setUsername(param.getUsername());
        user.setSalt(RandomStringUtils.secure().next(8, true, true));
        if (StringUtils.isNotBlank(param.getPassword())) {
            user.setPassword(PasswordUtil.encrypt(param.getUsername(), param.getPassword(), user.getSalt()));
        }
        user.setEnabled(param.getEnabled());
        user.setFullName(param.getFullName());
        user.setPhone(param.getPhone());
        user.setEmail(param.getEmail());
        user.setRemark(param.getRemark());

        this.saveOrUpdate(user);
        userRoleMapper.deleteByUserId(user.getId());
        if (param.getRoles() != null) {
            for (Integer role : param.getRoles()) {
                UserRole userRole = new UserRole();
                userRole.setRoleId(role);
                userRole.setUserId(user.getId());
                userRoleMapper.insert(userRole);
            }
        }
//        loginService.refreshUserInfo(user.getId());
    }

    public IPage<UserPage> getPage(UserPageParam param) {
        return baseMapper.selectPage(param.toPage(), new LambdaQueryWrapper<User>()
                .like(StringUtils.isNotBlank(param.getFullName()), User::getFullName, param.getFullName())
                .orderByDesc(User::getId))
                .convert(item -> convert.toUserPage(item));
    }

    public UserDetailVO detail(Integer id) {
        User user = getOptById(id).orElseThrow(() -> new BizException("用户不存在"));
        UserDetailVO userDetailVO = convert.toUserDetailVO(user);
        List<Role> roles = roleService.getRoleByUserId(id);
        userDetailVO.setRoleId(roles.stream().map(Role::getId).toList());
        userDetailVO.setRoleNames(roles.stream().map(Role::getName).toList());
        return userDetailVO;
    }

    @Transactional(rollbackOn = Exception.class)
    public void delete(Integer id) {
        removeById(id);
        userRoleMapper.deleteByUserId(id);
//        loginService.removeUserInfo(id);
    }
}
