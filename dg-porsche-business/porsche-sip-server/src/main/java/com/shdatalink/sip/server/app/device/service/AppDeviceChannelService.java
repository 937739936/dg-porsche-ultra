package com.shdatalink.sip.server.app.device.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.sip.server.app.device.vo.AppDeviceChannelDetailVO;
import com.shdatalink.sip.server.app.device.vo.AppDeviceChannelPage;
import com.shdatalink.sip.server.app.device.vo.AppDeviceChannelPageParam;
import com.shdatalink.sip.server.media.MediaService;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.mapper.DeviceChannelMapper;
import com.shdatalink.sip.server.module.device.mapper.DeviceMapper;
import com.shdatalink.sip.server.module.device.service.DeviceChannelService;
import com.shdatalink.sip.server.module.device.service.DeviceSnapService;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewSnapshot;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.app.device.service.AppDeviceChannelService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@ApplicationScoped
public class AppDeviceChannelService extends ServiceImpl<DeviceChannelMapper, DeviceChannel> {
    @Inject
    MediaService mediaService;
    @Inject
    DeviceSnapService deviceSnapService;
    @Inject
    DeviceChannelService deviceChannelService;
    @Inject
    DeviceMapper deviceMapper;

    public IPage<AppDeviceChannelPage> getPage(AppDeviceChannelPageParam param) {
        return baseMapper.selectPage(
                new Page<>(param.getPage(), param.getPageSize()),
                new LambdaQueryWrapper<DeviceChannel>()
                        .eq(DeviceChannel::getDeviceId, param.getDeviceId())
                        .isNotNull(DeviceChannel::getRegisterTime)
        ).convert(item -> {
            AppDeviceChannelPage page = new AppDeviceChannelPage();
            page.setId(item.getId());
            page.setName(item.getName());
            page.setChannelId(item.getChannelId());
            page.setDeviceId(item.getDeviceId());
            page.setOnline(item.getOnline());
            page.setLeaveTime(item.getLeaveTime());
            page.setPtzType(item.getPtzType());
            Device device = deviceMapper.selectByChannelId(item.getChannelId());
            page.setPlayUrl(mediaService.getPlayUrl(device, item));
            return page;
        });
    }

    public DevicePreviewSnapshot snapShot(String deviceId, String channelId) throws IOException {
        return deviceSnapService.querySnapshot(deviceId, channelId);
    }


    public AppDeviceChannelDetailVO detail(String deviceId, String channelId) {
        DeviceChannel channel = baseMapper.selectByDeviceIdAndChannelId(deviceId, channelId);
        if (channel == null) {
            throw new BizException("通道不存在");
        }

        Device device = deviceMapper.selectByChannelId(channel.getChannelId());
        AppDeviceChannelDetailVO page = new AppDeviceChannelDetailVO();
        page.setId(channel.getId());
        page.setName(channel.getName());
        page.setChannelId(channel.getChannelId());
        page.setDeviceId(channel.getDeviceId());
        page.setOnline(channel.getOnline());
        page.setLeaveTime(channel.getLeaveTime());
        page.setPtzType(channel.getPtzType());
        page.setPlayUrl(mediaService.getPlayUrl(device, channel));
        return page;
    }
}
