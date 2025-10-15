package com.shdatalink.sip.server.module.device.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.shdatalink.sip.server.module.device.entity.UserScreenDevice;
import com.shdatalink.sip.server.module.device.mapper.UserScreenDeviceMapper;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewPresetParam;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.device.service.UserScreenDeviceService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@Slf4j
@ApplicationScoped
public class UserScreenDeviceService extends ServiceImpl<UserScreenDeviceMapper, UserScreenDevice> {
    @Transactional
    public void save(List<DevicePreviewPresetParam.Device> deviceList, Integer presetId) {
        deviceList.forEach(d -> {
            UserScreenDevice screen = new UserScreenDevice();
            screen.setPresetId(presetId);
            if (d != null) {
                screen.setDeviceId(d.getDeviceId());
                screen.setChannelId(d.getChannelId());
            }
            save(screen);
        });
    }

    public List<UserScreenDevice> getByPresetId(Integer id) {
        return baseMapper.selectList(new LambdaQueryWrapper<UserScreenDevice>()
                .eq(UserScreenDevice::getPresetId, id));
    }

    public void deleteByPresetId(Integer id) {
        baseMapper.delete(new LambdaQueryWrapper<UserScreenDevice>()
                .eq(UserScreenDevice::getPresetId, id));
    }
}
