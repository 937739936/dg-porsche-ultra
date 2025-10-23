package com.shdatalink.sip.server.module.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.sip.server.module.user.entity.*;
import com.shdatalink.sip.server.module.user.mapper.*;
import com.shdatalink.sip.server.module.user.vo.RoleSaveParam;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.user.service.RoleService",
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

    public List<Permission> getPermissions(List<Integer> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(new LambdaQueryWrapper<RolePermission>()
                .in(RolePermission::getRoleId, roleIds));
        if (CollectionUtils.isEmpty(rolePermissions)) {
            return Collections.emptyList();
        }
        return permissionMapper.selectByIds(rolePermissions.stream().map(RolePermission::getPermissionId).collect(Collectors.toSet()));
    }

    public List<String> getPermissionDevice(List<Integer> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        return roleDeviceMapper.selectList(new LambdaQueryWrapper<RoleDevice>().in(RoleDevice::getRoleId, roleIds))
                .stream()
                .map(RoleDevice::getDeviceId)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackOn = Exception.class)
    public boolean save(RoleSaveParam param) {
        // 1、校验角色名称是否唯一
        if (checkRoleNameUnique(param)) {
            throw new BizException("角色名称已存在");
        }

        // 2、保存角色信息
        Role role = param.getId() == null ? new Role() : getOptById(param.getId()).orElseThrow(() -> new BizException("角色不存在"));
        role.setName(param.getName());
        return saveOrUpdate(role);
    }

    public List<Role> getRoleByUserId(Integer id) {
        List<UserRole> userRoles = userRoleMapper.selectByUserId(id);
        if (CollectionUtils.isEmpty(userRoles)) {
            return Collections.emptyList();
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
