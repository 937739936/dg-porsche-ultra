package com.shdatalink.sip.server.module.user.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.shdatalink.sip.server.module.user.entity.Permission;
import com.shdatalink.sip.server.module.user.mapper.PermissionMapper;
import com.shdatalink.sip.server.module.user.vo.PermissionAddParam;
import com.shdatalink.sip.server.module.user.vo.PermissionParam;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.user.service.PermissionService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@Slf4j
@ApplicationScoped
public class PermissionService extends ServiceImpl<PermissionMapper, Permission> {

    public boolean add(PermissionAddParam param) {
        add(param.getData(), null);
        return true;
    }

    private void add(List<PermissionParam> params, Integer parentId) {
        params.forEach(param -> {
            Permission permission = new Permission();
            permission.setPermission(param.getPermission());
            permission.setParentId(parentId);
            permission.setName(param.getName());
            save(permission);
            if (param.getChildren() != null) {
                add(param.getChildren(), permission.getId());
            }
        });
    }
}
