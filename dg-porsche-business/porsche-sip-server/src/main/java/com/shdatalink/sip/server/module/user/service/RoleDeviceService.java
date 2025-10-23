package com.shdatalink.sip.server.module.user.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.shdatalink.sip.server.module.user.entity.RoleDevice;
import com.shdatalink.sip.server.module.user.mapper.RoleDeviceMapper;
import com.shdatalink.sip.server.module.user.vo.RoleDeviceSaveParam;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.user.service.RoleDeviceService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@Slf4j
@ApplicationScoped
public class RoleDeviceService extends ServiceImpl<RoleDeviceMapper, RoleDevice> {

    @Transactional(rollbackOn = Exception.class)
    public boolean save(RoleDeviceSaveParam param) {
        baseMapper.deleteByRoleId(param.getRoleId());
        if (CollectionUtils.isEmpty(param.getDeviceIds())) {
            return false;
        }
        List<RoleDevice> list = param.getDeviceIds()
                .stream()
                .map(deviceId -> {
                    RoleDevice rd = new RoleDevice();
                    rd.setRoleId(param.getRoleId());
                    rd.setDeviceId(deviceId);
                    return rd;
                }).toList();
        return saveBatch(list);
    }
}
