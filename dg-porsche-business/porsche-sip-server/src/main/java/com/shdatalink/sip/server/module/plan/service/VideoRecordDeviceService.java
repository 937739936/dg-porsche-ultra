package com.shdatalink.sip.server.module.plan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shdatalink.framework.common.service.EventPublisher;
import com.shdatalink.sip.server.common.dto.PageParam;
import com.shdatalink.sip.server.common.dto.PageParamWithGet;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.mapper.DeviceChannelMapper;
import com.shdatalink.sip.server.module.device.mapper.DeviceMapper;
import com.shdatalink.sip.server.module.plan.convert.VideoRecordConvert;
import com.shdatalink.sip.server.module.plan.entity.VideoRecordDevice;
import com.shdatalink.sip.server.module.plan.event.PlanModifyEvent;
import com.shdatalink.sip.server.module.plan.mapper.VideoRecordDeviceMapper;
import com.shdatalink.sip.server.module.plan.vo.VideoDeviceBindList;
import com.shdatalink.sip.server.module.plan.vo.VideoRecordDevicePage;
import com.shdatalink.sip.server.module.plan.vo.VideoRecordDeviceParam;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@ApplicationScoped
public class VideoRecordDeviceService extends ServiceImpl<VideoRecordDeviceMapper, VideoRecordDevice> {

    @Inject
    DeviceMapper deviceMapper;
    @Inject
    DeviceChannelMapper deviceChannelMapper;
    @Inject
    VideoRecordConvert videoRecordConvert;
    @Inject
    EventPublisher publisher;

    @Transactional
    public void add(VideoRecordDeviceParam param) {
        baseMapper.deleteByPlanId(param.getPlanId());
        if (param.getDeviceList() != null) {
            for (VideoRecordDeviceParam.Device device : param.getDeviceList()) {
                VideoRecordDevice videoRecordDevice = new VideoRecordDevice();
                videoRecordDevice.setDeviceId(device.getDeviceId());
                videoRecordDevice.setChannelId(device.getChannelId());
                videoRecordDevice.setPlanId(param.getPlanId());
                baseMapper.insert(videoRecordDevice);
            }
        }
        publisher.fireAfterCommit(new PlanModifyEvent());
    }

    public IPage<VideoRecordDevicePage> getPage(PageParamWithGet param) {
        Page<VideoRecordDevice> page = baseMapper.selectPage(
                param.toPage(),
                new LambdaQueryWrapper<VideoRecordDevice>()
                        .exists("select 1 from t_device_channel where channel_id = t_video_record_device.channel_id")
                        .orderByDesc(VideoRecordDevice::getId)
                        .groupBy(VideoRecordDevice::getChannelId)
        );
        if (page.getRecords().isEmpty()) {
            return param.toPage();
        }
        Map<String, Device> deviceMap = deviceMapper.selectByDeviceIdList(page.getRecords().stream().map(VideoRecordDevice::getDeviceId).collect(Collectors.toSet()))
                .stream()
                .collect(Collectors.toMap(Device::getDeviceId, item -> item, (a, b) -> a));
        return page
                .convert(item -> {
                    VideoRecordDevicePage videoRecordDevice = videoRecordConvert.toPage(item);
                    if (deviceMap.containsKey(item.getDeviceId())) {
                        Device device = deviceMap.get(item.getDeviceId());
                        videoRecordDevice.setDeviceName(device.getName());
                        DeviceChannel channel = deviceChannelMapper.selectByDeviceIdAndChannelId(item.getDeviceId(), item.getChannelId());
                        videoRecordDevice.setChannelName(channel.getName());
                        videoRecordDevice.setOnline(channel.getOnline());
                        videoRecordDevice.setRecording(channel.getRecording());
                    }
                    return videoRecordDevice;
                });
    }


    public List<VideoDeviceBindList> getBindList(Integer planId) {
        return baseMapper.selectByPlanId(planId)
                .stream().map(item -> videoRecordConvert.to(item)).toList();
    }
}
