package com.shdatalink.sip.service.module.user.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.shdatalink.sip.service.module.user.entity.RoleDevice;
import com.shdatalink.sip.service.module.user.mapper.RoleDeviceMapper;
import com.shdatalink.sip.service.module.user.vo.RoleDeviceSaveParam;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.service.module.user.service.RoleDeviceService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@Slf4j
@ApplicationScoped
public class RoleDeviceService extends ServiceImpl<RoleDeviceMapper, RoleDevice> {

    @Transactional(rollbackOn = Exception.class)
    public boolean save(RoleDeviceSaveParam param) {
        baseMapper.deleteByRoleId(param.getRoleId());
        if (param.getDeviceIds() != null) {
            for (String deviceId : param.getDeviceIds()) {
                RoleDevice rd = new RoleDevice();
                rd.setRoleId(param.getRoleId());
                rd.setDeviceId(deviceId);
                save(rd);
            }
        }
        return true;
    }
}
