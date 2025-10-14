package com.shdatalink.sip.service.module.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.sip.service.module.user.entity.*;
import com.shdatalink.sip.service.module.user.mapper.*;
import com.shdatalink.sip.service.module.user.vo.RoleSaveParam;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.service.module.user.service.RoleService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@Slf4j
@ApplicationScoped
public class RoleService extends ServiceImpl<RoleMapper, Role> {

    @Inject
    RolePermissionMapper rolePermissionMapper;
    @Inject
    PermissionMapper permissionMapper;
    @Inject
    RoleDeviceMapper roleDeviceMapper;
    @Inject
    UserRoleMapper userRoleMapper;

    public List<Permission> getPermissions(List<Integer> roleId) {
        if (roleId == null || roleId.isEmpty()) {
            return new ArrayList<>();
        }
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(new LambdaQueryWrapper<RolePermission>()
                .in(RolePermission::getRoleId, roleId));
        if (CollectionUtils.isEmpty(rolePermissions)) {
            return Collections.emptyList();
        }
        return permissionMapper.selectByIds(rolePermissions.stream().map(RolePermission::getPermissionId).collect(Collectors.toSet()));
    }

    public List<String> getPermissionDevice(List<Integer> roleId) {
        if (roleId == null || roleId.isEmpty()) {
            return new ArrayList<>();
        }
        return roleDeviceMapper.selectList(new LambdaQueryWrapper<RoleDevice>()
                        .in(RoleDevice::getRoleId, roleId))
                .stream().map(RoleDevice::getDeviceId).collect(Collectors.toList());
    }

    public boolean save(RoleSaveParam param) {
        Role role;
        if (param.getId() == null) {
            role = new Role();
        } else {
            role = getOptById(param.getId()).orElseThrow(() -> new BizException("角色不存在"));
        }
        if (checkRoleNameUnique(param)) {
            throw new BizException("角色名称已存在");
        }

        role.setName(param.getName());
        saveOrUpdate(role);
        return true;
    }

    public List<Role> getRoleByUserId(Integer id) {
        List<UserRole> userRoles = userRoleMapper.selectByUserId(id);
        if (userRoles == null || userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        return baseMapper.selectByIds(userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toSet()));
    }

    /**
     * 校验角色名称是否唯一
     */
    public boolean checkRoleNameUnique(RoleSaveParam param) {
        Integer id = Objects.isNull(param.getId()) ? -1 : param.getId();
        return baseMapper.checkByName(id, param.getName()) > 0;
    }

    /**
     * 获取所有权限。
     *
     * @return 包含所有权限名称的列表
     */
    public List<String> getAllPermissions() {
        return permissionMapper.selectList(null)
                .stream().map(Permission::getPermission).toList();
    }
}
