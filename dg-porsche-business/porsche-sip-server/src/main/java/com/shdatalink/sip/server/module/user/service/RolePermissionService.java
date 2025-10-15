package com.shdatalink.sip.server.module.user.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.shdatalink.sip.server.module.user.entity.RolePermission;
import com.shdatalink.sip.server.module.user.mapper.RolePermissionMapper;
import com.shdatalink.sip.server.module.user.vo.RolePermissionSaveParam;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.user.service.RolePermissionService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@Slf4j
@ApplicationScoped
public class RolePermissionService extends ServiceImpl<RolePermissionMapper, RolePermission> {

    @Transactional
    public boolean save(RolePermissionSaveParam param) {
        baseMapper.deleteByRoleId(param.getRoleId());
        if (param.getPermissionIds() != null) {
            param.getPermissionIds().forEach(permissionId -> {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setPermissionId(permissionId);
                rolePermission.setRoleId(param.getRoleId());
                save(rolePermission);
            });
        }
        return true;
    }
}
