package com.shdatalink.sip.server.module.device.schedule;

import com.shdatalink.framework.common.service.EventPublisher;
import com.shdatalink.sip.server.gb28181.SipMessageTemplate;
import com.shdatalink.sip.server.gb28181.StreamFactory;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.response.DeviceStatus;
import com.shdatalink.sip.server.media.MediaService;
import com.shdatalink.sip.server.media.bean.entity.resp.MediaListResult;
import com.shdatalink.sip.server.module.alarmplan.entity.Subscribe;
import com.shdatalink.sip.server.module.alarmplan.service.SubscribeService;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import com.shdatalink.sip.server.module.device.event.DeviceOnlineEvent;
import com.shdatalink.sip.server.module.device.mapper.DeviceChannelMapper;
import com.shdatalink.sip.server.module.device.mapper.DeviceMapper;
import com.shdatalink.sip.server.module.device.service.DeviceChannelService;
import com.shdatalink.sip.server.module.device.service.DeviceService;
import com.shdatalink.framework.web.utils.IpUtil;
import com.shdatalink.sip.server.module.device.service.DeviceSnapService;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
@Startup
@Slf4j
public class DeviceSchedule {

    @Inject
    DeviceService deviceService;
    @Inject
    SipMessageTemplate sipMessageTemplate;
    @Inject
    EventPublisher publisher;
    @Inject
    DeviceChannelService deviceChannelService;
    @Inject
    MediaService mediaService;
    @Inject
    SubscribeService subscribeService;
    @Inject
    DeviceMapper deviceMapper;
    @Inject
    DeviceChannelMapper deviceChannelMapper;
    @Inject
    DeviceSnapService deviceSnapService;

    /**
     * 设备在线状态查询任务
     */
    @Scheduled(cron = "0 */10 * * * ?")
    @RunOnVirtualThread
    public void onlineCheck() {
        for (Device device : deviceService.list()) {
            if (!device.getEnable() || device.getRegisterTime() == null) {
                deviceService.updateOnline(device.getDeviceId(), false);
                deviceChannelService.setDeviceOffline(device.getDeviceId());
                continue;
            }

            if (device.getProtocolType() == ProtocolTypeEnum.GB28181) {
                try {
                    DeviceStatus status = sipMessageTemplate.getDeviceStatus(device.toGbDevice());
                    boolean online = "ONLINE".equals(status.getOnline());
                    deviceService.updateOnline(device.getDeviceId(), online);
                    if (!online) {
                        deviceChannelService.setDeviceOffline(device.getDeviceId());
                    } else {
                        deviceChannelService.renewalChannel(device.getDeviceId());
                    }
                    if (device.getOnline() != online) {
                        publisher.fireAsync(new DeviceOnlineEvent(device.getDeviceId(), online));
                    }
                } catch (Exception e) {
                    log.error("查询设备DeviceStatus信息出错:{}", e.getMessage());
                    deviceService.updateOnline(device.getDeviceId(), false);
                    deviceChannelService.setDeviceOffline(device.getDeviceId());
                    if (device.getOnline()) {
                        publisher.fireAsync(new DeviceOnlineEvent(device.getDeviceId(), false));
                    }
                }
            } else if (device.getProtocolType() == ProtocolTypeEnum.PULL) {
                for (DeviceChannel deviceChannel : deviceChannelMapper.selectByDeviceId(device.getDeviceId())) {
                    boolean online = IpUtil.validate(device.getStreamUrl());
                    if (device.getOnline() != online) {
                        publisher.fireAsync(new DeviceOnlineEvent(device.getDeviceId(), online));
                    }

                    device.setEnable(true);
                    device.setOnline(online);
                    if (online) {
                        device.setRegisterTime(LocalDateTime.now());
                        device.setKeepaliveTime(LocalDateTime.now());
                    }
                    deviceService.updateById(device);
                    deviceChannel.setEnable(true);
                    deviceChannel.setOnline(online);
                    deviceChannelMapper.updateById(deviceChannel);
                }
            }
        }
        log.info("设备在线状态查询任务完成");
    }

    @Scheduled(cron = "0 */15 * * * ?")
    public void snapshot() {
        Map<String, Device> deviceMap = deviceService.list().stream()
                .filter(Device::getEnable)
                .filter((Device::getOnline))
                .collect(Collectors.toMap(Device::getDeviceId, item -> item, (a, b) -> a));
        List<MediaListResult> mediaList = mediaService.listMedia();
        deviceChannelService.selectRegisterChannel()
                .stream()
                .filter(DeviceChannel::getOnline)
                .filter(item -> mediaList.stream().noneMatch(m -> StreamFactory.extractChannel(m.getStream()).equals(item.getId())))
                .forEach(channel -> {
                    if (!deviceMap.containsKey(channel.getDeviceId())) {
                        return;
                    }
                    Device device = deviceMap.get(channel.getDeviceId());
                    deviceSnapService.snapshot(device, channel);
                });
    }

    /**
     * 刷新订阅
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void updateSubscribeExpires() {
        List<Subscribe> list = subscribeService.list();
        for (Subscribe subscribe : list) {
            Device device = deviceMapper.selectByDeviceId(subscribe.getDeviceId());
            if (device == null) {
                continue;
            }
            LocalDateTime catalogTime = subscribe.getCatalogTime();
            if (subscribe.isCatalog() && (catalogTime == null || catalogTime.isBefore(LocalDateTime.now().plusMinutes(20)))) {
                try {
                    subscribeService.catalogSubscribe(device, subscribe.isCatalog());
                } catch (Exception e) {
                    log.error("刷新目录订阅时间失败，{}", e.getMessage());
                }
            }
            LocalDateTime positionTime = subscribe.getPositionTime();
            if (subscribe.isPosition() && (positionTime == null || positionTime.isBefore(LocalDateTime.now().plusMinutes(20)))) {
                try {
                    subscribeService.positionSubscribe(device, subscribe.isPosition());
                } catch (Exception e) {
                    log.error("刷新位置订阅时间失败，{}", e.getMessage());
                }
            }
            LocalDateTime alarmTime = subscribe.getAlarmTime();
            if (subscribe.isAlarm() && (alarmTime == null || alarmTime.isBefore(LocalDateTime.now().plusMinutes(20)))) {
                try {
                    subscribeService.alarmSubscribe(device, subscribe.isAlarm());
                } catch (Exception e) {
                    log.error("刷新报警订阅时间失败，{}", e.getMessage());
                }
            }
        }
    }
}
