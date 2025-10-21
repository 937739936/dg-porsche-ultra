package com.shdatalink.sip.server.app.device.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shdatalink.sip.server.app.device.vo.AppDevicePage;
import com.shdatalink.sip.server.app.device.vo.AppDevicePageParam;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.mapper.DeviceMapper;
import com.shdatalink.sip.server.module.device.service.DeviceService;
import com.shdatalink.sip.server.module.device.vo.PtzControlParam;
import com.shdatalink.sip.server.module.device.vo.PtzControlStopParam;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.lang3.StringUtils;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.app.device.service.AppDeviceService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@ApplicationScoped
public class AppDeviceService extends ServiceImpl<DeviceMapper, Device> {
    private final DeviceService deviceService;

    public AppDeviceService(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    public IPage<AppDevicePage> getPage(AppDevicePageParam param) {
        Page<Device> devicePage = baseMapper.selectPage(
                new Page<>(param.getPage(), param.getPageSize()),
                new LambdaQueryWrapper<Device>()
                        .eq(param.getOnline() != null, Device::getOnline, param.getOnline())
                        .isNotNull(Device::getRegisterTime)
                        .and(StringUtils.isNotBlank(param.getKeyword()), wrapper -> {
                            wrapper.like(Device::getDeviceId, param.getKeyword())
                                    .or().like(Device::getName, param.getKeyword());
                        })
        );
        return devicePage.convert(item -> {
            AppDevicePage devicePageResult = new AppDevicePage();
            devicePageResult.setId(item.getId());
            devicePageResult.setName(item.getName());
            devicePageResult.setDeviceId(item.getDeviceId());
            devicePageResult.setChannelCount(item.getChannelCount());
            devicePageResult.setManufacturer(item.getManufacturer());
            devicePageResult.setOnline(item.getOnline());
            devicePageResult.setIpaddr(item.getTransport()+"://"+item.getIp()+":"+item.getPort());
            devicePageResult.setDeviceType(item.getDeviceType());
            return devicePageResult;
        });
    }

    public boolean ptzControl(PtzControlParam param) {
        return deviceService.ptzControl(param);
    }

    public boolean ptzControlStart(PtzControlParam param) {
        return deviceService.ptzControlStart(param);
    }

    public boolean ptzControlStop(PtzControlStopParam param) {
        return deviceService.ptzControlStop(param);
    }
}
