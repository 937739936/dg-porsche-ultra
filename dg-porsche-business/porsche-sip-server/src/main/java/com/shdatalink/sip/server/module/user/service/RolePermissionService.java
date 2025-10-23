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
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.user.service.RolePermissionService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@Slf4j
@ApplicationScoped
public class RolePermissionService extends ServiceImpl<RolePermissionMapper, RolePermission> {

    @Transactional(rollbackOn = Exception.class)
    public boolean save(RolePermissionSaveParam param) {
        // 删除角色权限
        baseMapper.deleteByRoleId(param.getRoleId());

        // 保存角色权限
        if (CollectionUtils.isNotEmpty(param.getPermissionIds())) {
            return false;
        }
        List<RolePermission> list = param.getPermissionIds()
                .stream()
                .map(permissionId -> {
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setPermissionId(permissionId);
                    rolePermission.setRoleId(param.getRoleId());
                    return rolePermission;
                }).toList();
        return saveBatch(list);
    }


}
