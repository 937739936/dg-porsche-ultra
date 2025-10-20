package com.shdatalink.sip.server.integration.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.shdatalink.sip.server.integration.vo.IntegrationChannelOnlineStatusVO;
import com.shdatalink.sip.server.integration.vo.IntegrationDeviceDetail;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.mapper.DeviceMapper;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.integration.service.IntegrationDeviceService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@ApplicationScoped
public class IntegrationDeviceService extends ServiceImpl<DeviceMapper, Device> {
    public List<IntegrationDeviceDetail> queryByName(String name) {
        List<Device> devicePage = baseMapper.selectList(
                new LambdaQueryWrapper<Device>()
                        .isNotNull(Device::getRegisterTime)
                        .eq(Device::getName, name)
        );
        return devicePage.stream()
                .map(item -> {
                    IntegrationDeviceDetail detail = new IntegrationDeviceDetail();
                    detail.setDeviceId(item.getDeviceId());
                    detail.setName(item.getName());
                    detail.setChannelCount(item.getChannelCount());
                    detail.setManufacturer(item.getManufacturer());
                    detail.setOnline(item.getOnline());
                    detail.setIpaddr(item.getTransport()+"://"+item.getIp()+":"+item.getPort());
                    return detail;
                }).toList();
    }

    public List<IntegrationChannelOnlineStatusVO> queryOnline(List<String> deviceId) {
        return baseMapper.selectByDeviceIdList(deviceId)
                .stream()
                .map(device -> {
                    IntegrationChannelOnlineStatusVO vo = new IntegrationChannelOnlineStatusVO();
                    vo.setDeviceId(device.getDeviceId());
                    vo.setOnline(device.getOnline());
                    return vo;
                }).toList();
    }
}
