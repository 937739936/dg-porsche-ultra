package com.shdatalink.sip.server.integration.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.sip.server.config.SipConfigProperties;
import com.shdatalink.sip.server.integration.convert.IntegrationDeviceConvert;
import com.shdatalink.sip.server.integration.vo.*;
import com.shdatalink.sip.server.media.MediaService;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.mapper.DeviceChannelMapper;
import com.shdatalink.sip.server.module.device.service.DeviceService;
import com.shdatalink.sip.server.module.device.service.DeviceSnapService;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewPlayVO;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewSnapshot;
import com.shdatalink.sip.server.module.device.vo.PtzControlParam;
import com.shdatalink.sip.server.module.device.vo.PtzControlStopParam;
import com.shdatalink.sip.server.module.plan.service.VideoRecordRemoteService;
import com.shdatalink.sip.server.module.plan.vo.VideoRecordTimeLineVO;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.integration.service.IntegrationDeviceChannelService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@ApplicationScoped
public class IntegrationDeviceChannelService extends ServiceImpl<DeviceChannelMapper, DeviceChannel> {
    @Inject
    MediaService mediaService;
    @Inject
    SipConfigProperties sipConfigProperties;
    @Inject
    DeviceService deviceService;
    @Inject
    VideoRecordRemoteService videoRecordRemoteService;
    @Inject
    IntegrationDeviceConvert integrationDeviceConvert;
    @Inject
    DeviceSnapService deviceSnapService;

    public IPage<IntegrationDeviceChannelList> getPage(IntegrationChannelPageParam param) {
        return baseMapper.selectPage(
                param.toPage(),
                new LambdaQueryWrapper<DeviceChannel>()
                        .in(DeviceChannel::getDeviceId, param.getDeviceId())
                        .isNotNull(DeviceChannel::getRegisterTime)
        ).convert(item -> {
            Device device = deviceService.getByDeviceId(item.getDeviceId())
                    .orElseThrow(() -> new BizException("设备不存在"));
            IntegrationDeviceChannelList page = new IntegrationDeviceChannelList();
            page.setChannelId(item.getChannelId());
            page.setName(item.getName());
            page.setDeviceId(item.getDeviceId());
            page.setOnline(item.getOnline());
            page.setLeaveTime(item.getLeaveTime());
            page.setPtzType(item.getPtzType());
            page.setPlayUrl(mediaService.getPlayUrl(device, item));
            return page;
        });
    }

    public List<IntegrationDevicePreviewSnapshot> getSnapshot(List<String> channelId) {
        return channelId.stream()
                .map(c -> {
                    IntegrationDevicePreviewSnapshot snapshot = new IntegrationDevicePreviewSnapshot();
                    DevicePreviewSnapshot devicePreviewSnapshot = deviceSnapService.realTimeSnap(c);
                    snapshot.setCreateTime(devicePreviewSnapshot.getCreateTime());
                    snapshot.setBase64(devicePreviewSnapshot.getBase64());
                    snapshot.setChannelId(c);
                    return snapshot;
                }).toList();
    }

    public List<IntegrationChannelOnlineStatusVO> queryOnline(List<String> channelId) {
        return baseMapper.selectByChannelIds(channelId)
                .stream()
                .map(channel -> {
                    IntegrationChannelOnlineStatusVO onlineStatus = new IntegrationChannelOnlineStatusVO();
                    onlineStatus.setChannelId(channel.getChannelId());
                    onlineStatus.setOnline(channel.getOnline());
                    onlineStatus.setDeviceId(channel.getDeviceId());
                    onlineStatus.setOfflineTime(channel.getLeaveTime());
                    return onlineStatus;
                }).toList();
    }

    public boolean ptzControl(IntegrationPtzControlParam param) {
        DeviceChannel channel = baseMapper.selectByChannelId(param.getChannelId());
        PtzControlParam ptzControlParam = integrationDeviceConvert.toPtzControlParam(param);
        ptzControlParam.setDeviceId(channel.getDeviceId());
        return deviceService.ptzControl(ptzControlParam);
    }

    public boolean ptzControlStart(IntegrationPtzControlStartParam param) {
        DeviceChannel channel = baseMapper.selectByChannelId(param.getChannelId());
        PtzControlParam ptzControlParam = integrationDeviceConvert.toPtzControlParam(param);
        ptzControlParam.setDeviceId(channel.getDeviceId());
        return deviceService.ptzControlStart(ptzControlParam);
    }

    public boolean ptzControlStop(IntegrationPtzControlStopParam param) {
        DeviceChannel channel = baseMapper.selectByChannelId(param.getChannelId());
        PtzControlStopParam ptzControlParam = integrationDeviceConvert.toPtzControlParam(param);
        ptzControlParam.setDeviceId(channel.getDeviceId());
        return deviceService.ptzControlStop(ptzControlParam);
    }

    public List<IntegrationDevicePreviewPlayVO> playUrl(List<String> channelId) {
        return channelId.stream()
                .map(c -> {
                    DeviceChannel channel = baseMapper.selectByChannelId(c);
                    Device device = deviceService.getByDeviceId(channel.getDeviceId()).orElseThrow(() -> new BizException("设备不存在"));
                    DevicePreviewPlayVO vo = mediaService.getPlayUrl(device, channel);
                    return integrationDeviceConvert.toPlayVO(vo);
                }).toList();
    }

    public IntegrationDevicePreviewPlayVO playbackUrl(String channelId, LocalDateTime start) {
        DeviceChannel channel = baseMapper.selectByChannelId(channelId);
        Device device = deviceService.getByDeviceId(channel.getDeviceId()).orElseThrow(() -> new BizException("设备不存在"));
        DevicePreviewPlayVO vo = mediaService.getPlayUrl(device, channel);
        return integrationDeviceConvert.toPlayVO(vo);
    }

    public List<VideoRecordTimeLineVO> playbackTimeline(String channelId, LocalDate date) {
        DeviceChannel channel = baseMapper.selectByChannelId(channelId);
        return videoRecordRemoteService.timeline(channel.getDeviceId(), channelId, date);
    }
}
