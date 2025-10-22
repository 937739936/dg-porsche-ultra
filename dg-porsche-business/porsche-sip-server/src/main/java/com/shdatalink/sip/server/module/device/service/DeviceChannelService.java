package com.shdatalink.sip.server.module.device.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.framework.common.service.EventPublisher;
import com.shdatalink.sip.server.config.SipConfigProperties;
import com.shdatalink.sip.server.gb28181.SipMessageTemplate;
import com.shdatalink.sip.server.gb28181.StreamFactory;
import com.shdatalink.sip.server.gb28181.core.bean.constants.InviteTypeEnum;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify.response.DeviceNotifyCatalog;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.response.DeviceCatalog;
import com.shdatalink.sip.server.gb28181.core.builder.GBRequest;
import com.shdatalink.sip.server.media.MediaService;
import com.shdatalink.sip.server.module.alarmplan.enums.DeviceEventEnum;
import com.shdatalink.sip.server.module.device.convert.DeviceChannelConvert;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import com.shdatalink.sip.server.module.device.enums.PtzTypeEnum;
import com.shdatalink.sip.server.module.device.enums.SIPProtocolEnum;
import com.shdatalink.sip.server.module.device.event.ChannelUpdateEvent;
import com.shdatalink.sip.server.module.device.event.DeviceOnlineEvent;
import com.shdatalink.sip.server.module.device.mapper.DeviceChannelMapper;
import com.shdatalink.sip.server.module.device.mapper.DeviceMapper;
import com.shdatalink.sip.server.module.device.vo.DeviceChannelPage;
import com.shdatalink.sip.server.module.device.vo.DeviceChannelPageParam;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewInfoVO;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.device.service.DeviceChannelService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@Slf4j
@ApplicationScoped
public class DeviceChannelService extends ServiceImpl<DeviceChannelMapper, DeviceChannel> {

    @Inject
    SipMessageTemplate sipMessageTemplate;
    @Inject
    DeviceService deviceService;
    @Inject
    EventPublisher publisher;
    @Inject
    DeviceMapper deviceMapper;
    @Inject
    DeviceChannelConvert deviceChannelConvert;
    @Inject
    MediaService mediaService;

    public Optional<DeviceChannel> findByDeviceIdAndChannelId(String deviceId, String channelId) {
        return baseMapper.selectOptByDeviceIdAndChannelId(deviceId, channelId);
    }

    @Transactional(rollbackOn = Exception.class)
    public Integer  renewalChannel(String deviceId) {
        Device device = deviceService.getByDeviceId(deviceId).orElseThrow(() -> new BizException("设备不存在"));
        DeviceCatalog deviceCatalog = sipMessageTemplate.getDeviceCatalog(device.toGbDevice());
        List<DeviceCatalog.DeviceCatalogList.DeviceCatalogItem> deviceList = deviceCatalog.getDeviceList().getDeviceList();
        for (DeviceCatalog.DeviceCatalogList.DeviceCatalogItem deviceCatalogItem : deviceList) {
            findByDeviceIdAndChannelId(deviceId, deviceCatalogItem.getDeviceId())
                    .ifPresent(channel -> {
                        if (channel.getRegisterTime() == null) {
                            channel.setRegisterTime(LocalDateTime.now());
                            channel.setName(deviceCatalogItem.getName());
                            publisher.fireAsync(new ChannelUpdateEvent(channel, ChannelUpdateEvent.Type.New));
                        }
                        if (channel.getPtzType() == null && deviceCatalogItem.getInfo() != null) {
                            channel.setPtzType(PtzTypeEnum.getByIdentifier(deviceCatalogItem.getInfo().getPTZType()));
                        }
                        boolean online = "ON".equals(deviceCatalogItem.getStatus());
                        if (online != channel.getOnline()) {
                            publisher.fireAsync(new DeviceOnlineEvent(deviceId, deviceCatalogItem.getDeviceId(), online));
                        }
                        channel.setOnline(online);
                        updateById(channel);
                        updateOnline(channel.getId(), online);
                    });
        }
        baseMapper.selectByDeviceId(deviceId)
                .stream()
                .filter(item -> deviceList.stream().noneMatch(c -> c.getDeviceId().equals(item.getChannelId())))
                .findFirst()
                .ifPresent(item -> {
                    if (item.getRegisterTime() == null) {
                        return;
                    }
                    if (item.getOnline()) {
                        updateOnline(item.getId(), false);
                        publisher.fireAsync(new DeviceOnlineEvent(deviceId, item.getChannelId(), false));
                    }
                });
        return deviceCatalog.getSumNum();
    }

    public IPage<DeviceChannelPage> getPage(DeviceChannelPageParam param) {
        return baseMapper.selectPage(param.toPage(), new LambdaQueryWrapper<DeviceChannel>()
                .eq(DeviceChannel::getDeviceId, param.getDeviceId())
                .eq(param.getOnline() != null, DeviceChannel::getOnline, param.getOnline())
                .eq(StringUtils.isNotBlank(param.getName()), DeviceChannel::getName, param.getName()))
                .convert(item -> {
                    DeviceChannelPage deviceChannelPage = deviceChannelConvert.toDeviceChannelPage(item);
                    Device device = deviceMapper.selectByChannelId(item.getChannelId());
                    if(device != null){
                        deviceChannelPage.setProtocolType(device.getProtocolType());
                    }
                    return deviceChannelPage;
                });
    }

    public boolean delete(String deviceId, String channelId) {
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<DeviceChannel>()
                .eq(DeviceChannel::getDeviceId, deviceId));
        if (count == 1) {
            throw new BizException("单通道请到设备列表删除设备");
        }

        baseMapper.delete(new LambdaQueryWrapper<DeviceChannel>()
                .eq(DeviceChannel::getDeviceId, deviceId)
                .eq(DeviceChannel::getChannelId, channelId));
        return true;
    }

    public DevicePreviewInfoVO info(String deviceId, String channelId) {
        Device device = deviceService.getByDeviceId(deviceId).orElseThrow(() -> new BizException("设备不存在"));

        DeviceChannel channel = baseMapper.selectByDeviceIdAndChannelId(deviceId, channelId);
        if (channel == null) {
            throw new BizException("通道不存在");
        }
        DevicePreviewInfoVO infoVO = new DevicePreviewInfoVO();
        infoVO.setTransportType(device.getStreamMode() == null ? null : device.getStreamMode().getTransportType());
        if(device.getProtocolType() == ProtocolTypeEnum.GB28181){
            infoVO.setProtocol(SIPProtocolEnum.GBT28181);
        }else if(device.getProtocolType() == ProtocolTypeEnum.PULL){
            infoVO.setProtocol(SIPProtocolEnum.PULL);
        }else if(device.getProtocolType() == ProtocolTypeEnum.RTMP){
            infoVO.setProtocol(SIPProtocolEnum.RTMP);
        }
        infoVO.setDeviceChannel(channelId);
        infoVO.setPlatformChannel(deviceId+"_"+channelId);
        infoVO.setPtzType(channel.getPtzType());
        return infoVO;
    }

    public List<DeviceChannel> selectRegisterChannel() {
        return baseMapper.selectList(new LambdaQueryWrapper<DeviceChannel>()
                .isNotNull(DeviceChannel::getRegisterTime));
    }

    public String getStream(String deviceId, String channelId) {
        return deviceId+"_"+channelId;
    }

    public void play(String streamId) {
        DeviceChannel channel = baseMapper.selectById(StreamFactory.extractChannel(streamId));
        if (channel == null) {
            throw new BizException("通道不存在");
        }
        Device device = deviceService.getByDeviceId(channel.getDeviceId()).orElseThrow(() -> new BizException("设备不存在"));
        mediaService.play(device.toGbDevice(channel.getChannelId()), streamId);

    }

    public void stop(InviteTypeEnum type, String deviceId, String channelId) {
        DeviceChannel channel = baseMapper.selectByDeviceIdAndChannelId(deviceId, channelId);
        if (channel == null) {
            return;
        }
        String streamId = StreamFactory.streamId(type, channel.getId().toString());
        Device device = deviceService.getByDeviceId(channel.getDeviceId()).orElseThrow(() -> new BizException("设备不存在"));
        GBRequest.bye(device.toGbDevice(channel.getChannelId())).withStreamId(streamId).execute();
        mediaService.closeStreams(streamId);
    }

    public void playBack(String streamId, LocalDateTime startTime, LocalDateTime endTime) {
        DeviceChannel channel = baseMapper.selectById(StreamFactory.extractChannel(streamId));
        if (channel == null) {
            throw new BizException("通道不存在");
        }
        Device device = deviceService.getByDeviceId(channel.getDeviceId()).orElseThrow(() -> new BizException("设备不存在"));
        mediaService.playback(device.toGbDevice(channel.getChannelId()), streamId, startTime, endTime);
    }

    public void setDeviceOffline(String deviceId) {
        baseMapper.setDeviceOffline(deviceId);
        baseMapper.selectByDeviceId(deviceId)
                .forEach(channel -> {
                    updateOnline(channel.getId(), false);
                    if (channel.getRegisterTime() != null) {
                        publisher.fireAsync(new DeviceOnlineEvent(channel.getDeviceId(), channel.getChannelId(), false));
                    }
                });
    }

    public void updateOnline(Integer id, boolean isOnline) {
        update(new LambdaUpdateWrapper<DeviceChannel>()
                .set(DeviceChannel::getOnline, isOnline)
                .set(!isOnline, DeviceChannel::getLeaveTime, LocalDateTime.now())
                .set(isOnline, DeviceChannel::getLeaveTime, null)
                .eq(DeviceChannel::getId, id)
                .isNull(!isOnline, DeviceChannel::getLeaveTime)
                .isNotNull(DeviceChannel::getRegisterTime)
        );
    }

    public void updateRecording(Integer id, boolean recording) {
        update(new LambdaUpdateWrapper<DeviceChannel>()
                .set(DeviceChannel::getRecording, recording)
                .eq(DeviceChannel::getId, id)
        );
    }

    @Transactional(rollbackOn = Exception.class)
    public void renewalChannelEvent(String deviceId, List<DeviceNotifyCatalog.DeviceCatalogList.DeviceCatalogItem> deviceList) {
        for (DeviceNotifyCatalog.DeviceCatalogList.DeviceCatalogItem deviceCatalogItem : deviceList) {
            Optional<DeviceChannel> deviceChannelOptional = findByDeviceIdAndChannelId(deviceId, deviceCatalogItem.getDeviceId());
            if (DeviceEventEnum.ADD.name().equals(deviceCatalogItem.getEvent()) || DeviceEventEnum.DEL.name().equals(deviceCatalogItem.getEvent()) || DeviceEventEnum.UPDATE.name().equals(deviceCatalogItem.getEvent()) || DeviceEventEnum.ON.name().equals(deviceCatalogItem.getEvent()) || DeviceEventEnum.OFF.name().equals(deviceCatalogItem.getEvent())) {
                if(deviceChannelOptional.isPresent()){
                    deviceChannelOptional.ifPresent(channel -> {
                        if (channel.getRegisterTime() == null) {
                            channel.setRegisterTime(LocalDateTime.now());
                            channel.setName(deviceCatalogItem.getName());
                            publisher.fireAsync(new ChannelUpdateEvent(channel, ChannelUpdateEvent.Type.New));
                        }
                        if (deviceCatalogItem.getInfo() != null) {
                            channel.setPtzType(PtzTypeEnum.getByIdentifier(deviceCatalogItem.getInfo().getPTZType()));
                        }
                        boolean online = DeviceEventEnum.ON.name().equals(deviceCatalogItem.getEvent()) || DeviceEventEnum.ADD.name().equals(deviceCatalogItem.getEvent()) || DeviceEventEnum.UPDATE.name().equals(deviceCatalogItem.getEvent()) && DeviceEventEnum.ON.name().equals(deviceCatalogItem.getStatus());
                        if (online != channel.getOnline()) {
                            channel.setOnline(online);
                            publisher.fireAsync(new DeviceOnlineEvent(deviceId, deviceCatalogItem.getDeviceId(), online));
                            if (!online) {
                                channel.setLeaveTime(LocalDateTime.now());
                            }
                        }
                        updateById(channel);
                    });
                }else{
                    renewalChannel(deviceId);
                }
            }else{
                throw new BizException("未知的事件类型");
            }
        }
    }
}
