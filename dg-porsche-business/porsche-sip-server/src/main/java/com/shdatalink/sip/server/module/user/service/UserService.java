package com.shdatalink.sip.server.module.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.framework.common.utils.PasswordUtil;
import com.shdatalink.framework.json.utils.JsonUtil;
import com.shdatalink.framework.redis.utils.RedisUtil;
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
import org.apache.commons.collections4.CollectionUtils;
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
    LoginService loginService;
    @Inject
    RedisUtil redisUtil;
    @Inject
    UserConvert convert;


    public UserInfo getUserInfo(Integer userId) {
        String userInfoStr = redisUtil.get(RedisKeyConstants.USER_LOGIN_INFO + userId);
        if (StringUtils.isBlank(userInfoStr)) {
            return null;
        }
        return JsonUtil.parseObject(userInfoStr, UserInfo.class);
    }

    public Optional<User> getUserByUserName(String username) {
        return this.getOneOpt(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    @Transactional(rollbackOn = Exception.class)
    public void saveUser(UserSaveParam param) {
        // 1、参数校验
        if (param.getId() == null && StringUtils.isBlank(param.getPassword())) {
            throw new BizException("密码不能为空");
        }

        // 2、保存用户信息
        User user = param.getId() == null ? new User() : getOptById(param.getId()).orElseThrow(() -> new BizException("用户不存在"));
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

        // 3、保存用户角色信息
        saveUserRole(user.getId(), param.getRoles());

        // 4、刷新用户信息
        loginService.refreshUserInfo(user.getId());
    }


    public IPage<UserPage> getPage(UserPageParam param) {
        return baseMapper.selectPage(param.toPage(), new LambdaQueryWrapper<User>()
                        .like(StringUtils.isNotBlank(param.getFullName()), User::getFullName, param.getFullName())
                        .orderByDesc(User::getId))
                .convert(item -> convert.toUserPage(item));
    }

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     */
    public UserDetailVO detail(Integer id) {
        // 通过用户ID获取用户信息，如果不存在则抛出异常
        User user = getOptById(id).orElseThrow(() -> new BizException("用户不存在"));
        // 通过用户ID获取用户角色列表
        List<Role> roles = roleService.getRoleByUserId(id);
        // 获取角色ID列表
        List<Integer> roleIds = roles.stream().map(Role::getId).toList();
        // 获取角色名称列表
        List<String> roleNames = roles.stream().map(Role::getName).toList();
        // 将用户信息和角色信息封装到VO对象中
        return convert.toUserDetailVO(user, roleIds, roleNames);
    }


    /**
     * 删除用户及其角色信息
     *
     * @param id 用户ID
     */
    @Transactional(rollbackOn = Exception.class)
    public void delete(Integer id) {
        // 1、删除用户信息
        removeById(id);
        // 2、删除用户角色信息
        deleteUserRole(id);
        // 3、删除用户缓存
        loginService.removeUserInfo(id);

    }

    /**
     * 保存用户角色信息
     *
     * @param userId  用户ID
     * @param roleIds 角色ID
     */
    private void saveUserRole(Integer userId, List<Integer> roleIds) {
        // 删除用户已存在的角色信息
        deleteUserRole(userId);

        // 保存用户角色信息
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }
        List<UserRole> userRoleList = roleIds.stream().map(roleId -> {
            UserRole userRole = new UserRole();
            userRole.setRoleId(roleId);
            userRole.setUserId(userId);
            return userRole;
        }).toList();
        userRoleMapper.insert(userRoleList);
    }


    /**
     * 删除指定用户的角色信息
     *
     * @param userId 用户ID
     */
    private void deleteUserRole(Integer userId) {
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
    }
}
