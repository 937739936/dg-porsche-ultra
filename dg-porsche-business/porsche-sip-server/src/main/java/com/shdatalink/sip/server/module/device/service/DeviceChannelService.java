package com.shdatalink.sip.server.module.device.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.sip.server.config.SipConfigProperties;
import com.shdatalink.sip.server.gb28181.core.bean.constants.InviteTypeEnum;
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
import com.shdatalink.sip.server.module.device.vo.DevicePreviewSnapshot;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;
import java.util.Optional;


@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.device.service.DeviceChannelService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@Slf4j
@ApplicationScoped
public class DeviceChannelService extends ServiceImpl<DeviceChannelMapper, DeviceChannel> {

//    @Inject
//    SipMessageTemplate sipMessageTemplate;
    @Inject
    DeviceService deviceService;
//    @Inject
//    MediaHttpClient mediaHttpClient;
    @Inject
    SipConfigProperties sipConfigProperties;
    @Inject
    DevicePlayService devicePlayService;
    @Inject
    DeviceMapper deviceMapper;

    public Optional<DeviceChannel> findByDeviceIdAndChannelId(String deviceId, String channelId) {
        return baseMapper.selectOptByDeviceIdAndChannelId(deviceId, channelId);
    }

//    @Transactional(rollbackOn = Exception.class)
//    public Integer  renewalChannel(String deviceId) {
//        Device device = deviceService.getByDeviceId(deviceId).orElseThrow(() -> new BizException("设备不存在"));
//        DeviceCatalog deviceCatalog = sipMessageTemplate.getDeviceCatalog(device.toGbDevice());
//        List<DeviceCatalog.DeviceCatalogList.DeviceCatalogItem> deviceList = deviceCatalog.getDeviceList().getDeviceList();
//        for (DeviceCatalog.DeviceCatalogList.DeviceCatalogItem deviceCatalogItem : deviceList) {
//            findByDeviceIdAndChannelId(deviceId, deviceCatalogItem.getDeviceId())
//                    .ifPresent(channel -> {
//                        if (channel.getRegisterTime() == null) {
//                            channel.setRegisterTime(LocalDateTime.now());
//                            channel.setName(deviceCatalogItem.getName());
//                            publisher.publishEvent(new ChannelUpdateEvent(channel, ChannelUpdateEvent.Type.New));
//                        }
//                        if (channel.getPtzType() == null && deviceCatalogItem.getInfo() != null) {
//                            channel.setPtzType(PtzTypeEnum.getByIdentifier(deviceCatalogItem.getInfo().getPTZType()));
//                        }
//                        boolean online = "ON".equals(deviceCatalogItem.getStatus());
//                        channel.setOnline(online);
//                        if (online != channel.getOnline()) {
//                            publisher.publishEvent(new DeviceOnlineEvent(deviceId, deviceCatalogItem.getDeviceId(), online));
//                        }
//                        updateById(channel);
//                        updateOnline(channel.getId(), online);
//                    });
//        }
//        baseMapper.selectByDeviceId(deviceId)
//                .stream()
//                .filter(item -> deviceList.stream().noneMatch(c -> c.getDeviceId().equals(item.getChannelId())))
//                .findFirst()
//                .ifPresent(item -> {
//                    updateOnline(item.getId(), false);
//                    publisher.publishEvent(new DeviceOnlineEvent(deviceId, item.getChannelId(), false));
//                });
//        return deviceCatalog.getSumNum();
//    }
//
//
//    public IPage<DeviceChannelPage> getPage(DeviceChannelPageParam param) {
//        return baseMapper.selectPage(param.toPage(), new LambdaQueryWrapper<DeviceChannel>()
//                .eq(DeviceChannel::getDeviceId, param.getDeviceId())
//                .eq(StringUtils.isNotBlank(param.getName()), DeviceChannel::getName, param.getName()))
//                .convert(item -> {
//                    DeviceChannelPage deviceChannelPage = new DeviceChannelPage();
//                    BeanUtils.copyProperties(item, deviceChannelPage);
//                    Device device = deviceMapper.selectByChannelId(item.getChannelId());
//                    if(device != null){
//                        deviceChannelPage.setProtocolType(device.getProtocolType());
//                    }
//                    return deviceChannelPage;
//                });
//    }
//
//    public boolean delete(String deviceId, String channelId) {
//        Long count = baseMapper.selectCount(new LambdaQueryWrapper<DeviceChannel>()
//                .eq(DeviceChannel::getDeviceId, deviceId));
//        if (count == 1) {
//            throw new BizException("单通道请到设备列表删除设备");
//        }
//
//        baseMapper.delete(new LambdaQueryWrapper<DeviceChannel>()
//                .eq(DeviceChannel::getDeviceId, deviceId)
//                .eq(DeviceChannel::getChannelId, channelId));
//        return true;
//    }
//
//    public DevicePreviewInfoVO info(String deviceId, String channelId) {
//        Device device = deviceService.getByDeviceId(deviceId).orElseThrow(() -> new BizException("设备不存在"));
//
//        DeviceChannel channel = baseMapper.selectByDeviceIdAndChannelId(deviceId, channelId);
//        if (channel == null) {
//            throw new BizException("通道不存在");
//        }
//        DevicePreviewInfoVO infoVO = new DevicePreviewInfoVO();
//        infoVO.setTransportType(device.getStreamMode() == null ? null : device.getStreamMode().getTransportType());
//        if(device.getProtocolType() == ProtocolTypeEnum.GB28181){
//            infoVO.setProtocol(SIPProtocolEnum.GBT28181);
//        }else if(device.getProtocolType() == ProtocolTypeEnum.PULL){
//            infoVO.setProtocol(SIPProtocolEnum.PULL);
//        }else if(device.getProtocolType() == ProtocolTypeEnum.RTMP){
//            infoVO.setProtocol(SIPProtocolEnum.RTMP);
//        }
//        infoVO.setDeviceChannel(channelId);
//        infoVO.setPlatformChannel(deviceId+"_"+channelId);
//        infoVO.setPtzType(channel.getPtzType());
//        return infoVO;
//    }
//
//    public List<DeviceChannel> selectRegisterChannel() {
//        return baseMapper.selectList(new LambdaQueryWrapper<DeviceChannel>()
//                .isNotNull(DeviceChannel::getRegisterTime));
//    }
//
//    public String getStream(String deviceId, String channelId) {
//        return deviceId+"_"+channelId;
//    }
//
//    public DevicePreviewSnapshot snapShot(String deviceId, String channelId) throws IOException {
//        DevicePreviewSnapshot snapshot = new DevicePreviewSnapshot();
//        String stream = getStream(deviceId, channelId);
//        String snapPath = sipConfigProperties.getMedia().getSnapPath();
//        Path path = Paths.get(snapPath, stream + ".jpg");
//        if (Files.exists(path)) {
//            byte[] bytes = Files.readAllBytes(path);
//            snapshot.setBase64(Base64.getEncoder().encodeToString(bytes));
//            BasicFileAttributes basicFileAttributes = Files.readAttributes(path, BasicFileAttributes.class);
//            if (basicFileAttributes != null && basicFileAttributes.isRegularFile()) {
//                snapshot.setCreateTime(basicFileAttributes.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
//            }
//        }
//        return snapshot;
//    }
//
//    public void play(String streamId) {
//        DeviceChannel channel = baseMapper.selectById(devicePlayService.extractStream(streamId));
//        if (channel == null) {
//            throw new BizException("通道不存在");
//        }
//        Device device = deviceService.getByDeviceId(channel.getDeviceId()).orElseThrow(() -> new BizException("设备不存在"));
//        List<RtpServer> rtpServers = mediaHttpClient.listRtpServer();
//        if (rtpServers == null || rtpServers.stream().noneMatch(s -> s.getStreamId().equals(streamId))) {
//            OpenRtpServerReq req = new OpenRtpServerReq();
//            req.setPort(0);
//            req.setTcpMode(1);
//            req.setStreamId(streamId);
//            OpenRtpServerResult result = mediaHttpClient.openRtpServer(req);
//            GBRequest.invite(device.toGbDevice(channel.getChannelId()))
//                    .withStreamId(streamId)
//                    .withMediaAddress(sipConfigProperties.getServer().getWanIp(), result.getPort())
//                    .play()
//                    .execute();
//        }
//
//    }
//
//    public void stop(InviteTypeEnum type, String deviceId, String channelId) {
//        DeviceChannel channel = baseMapper.selectByDeviceIdAndChannelId(deviceId, channelId);
//        if (channel == null) {
//            return;
//        }
//        String streamId = devicePlayService.streamId(type, channel.getId().toString());
//        Device device = deviceService.getByDeviceId(channel.getDeviceId()).orElseThrow(() -> new BizException("设备不存在"));
//        GBRequest.bye(device.toGbDevice(channel.getChannelId())).withStreamId(streamId).execute();
//        mediaHttpClient.closeStreams(new CloseStreamsReq(streamId));
//    }
//
//    public void playBack(String streamId, LocalDateTime startTime, LocalDateTime endTime) {
//        DeviceChannel channel = baseMapper.selectById(devicePlayService.extractStream(streamId));
//        if (channel == null) {
//            throw new BizException("通道不存在");
//        }
//        Device device = deviceService.getByDeviceId(channel.getDeviceId()).orElseThrow(() -> new BizException("设备不存在"));
//        List<RtpServer> rtpServers = mediaHttpClient.listRtpServer();
//        if (rtpServers == null || rtpServers.stream().noneMatch(s -> s.getStreamId().equals(streamId))) {
//            OpenRtpServerReq req = new OpenRtpServerReq();
//            req.setPort(0);
//            req.setTcpMode(1);
//            req.setStreamId(streamId);
//            OpenRtpServerResult result = mediaHttpClient.openRtpServer(req);
//
//            GBRequest.invite(device.toGbDevice(channel.getChannelId()))
//                    .withStreamId(streamId)
//                    .withMediaAddress(sipConfigProperties.getServer().getWanIp(), result.getPort())
//                    .playBack(startTime, endTime)
//                    .execute();
//        }
//    }
//
//    public void setDeviceOffline(String deviceId) {
//        baseMapper.setDeviceOffline(deviceId);
//    }
//
//    public void updateOnline(Integer id, boolean isOnline) {
//        update(new LambdaUpdateWrapper<DeviceChannel>()
//                .set(DeviceChannel::getOnline, isOnline)
//                .set(!isOnline, DeviceChannel::getLeaveTime, LocalDateTime.now())
//                .set(isOnline, DeviceChannel::getLeaveTime, null)
//                .eq(DeviceChannel::getId, id)
//                .isNull(!isOnline, DeviceChannel::getLeaveTime)
//                .isNotNull(DeviceChannel::getRegisterTime)
//        );
//    }
//
//    public void updateRecording(Integer id, boolean recording) {
//        update(new LambdaUpdateWrapper<DeviceChannel>()
//                .set(DeviceChannel::getRecording, recording)
//                .eq(DeviceChannel::getId, id)
//        );
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    public void renewalChannelEvent(String deviceId, List<DeviceNotifyCatalog.DeviceCatalogList.DeviceCatalogItem> deviceList) {
//        for (DeviceNotifyCatalog.DeviceCatalogList.DeviceCatalogItem deviceCatalogItem : deviceList) {
//            Optional<DeviceChannel> deviceChannelOptional = findByDeviceIdAndChannelId(deviceId, deviceCatalogItem.getDeviceId());
//            if (DeviceEventEnum.ADD.name().equals(deviceCatalogItem.getEvent()) || DeviceEventEnum.DEL.name().equals(deviceCatalogItem.getEvent()) || DeviceEventEnum.UPDATE.name().equals(deviceCatalogItem.getEvent()) || DeviceEventEnum.ON.name().equals(deviceCatalogItem.getEvent()) || DeviceEventEnum.OFF.name().equals(deviceCatalogItem.getEvent())) {
//                if(deviceChannelOptional.isPresent()){
//                    deviceChannelOptional.ifPresent(channel -> {
//                        if (channel.getRegisterTime() == null) {
//                            channel.setRegisterTime(LocalDateTime.now());
//                            channel.setName(deviceCatalogItem.getName());
//                            publisher.publishEvent(new ChannelUpdateEvent(channel, ChannelUpdateEvent.Type.New));
//                        }
//                        if (deviceCatalogItem.getInfo() != null) {
//                            channel.setPtzType(PtzTypeEnum.getByIdentifier(deviceCatalogItem.getInfo().getPTZType()));
//                        }
//                        boolean online = DeviceEventEnum.ON.name().equals(deviceCatalogItem.getEvent()) || DeviceEventEnum.ADD.name().equals(deviceCatalogItem.getEvent()) || DeviceEventEnum.UPDATE.name().equals(deviceCatalogItem.getEvent()) && DeviceEventEnum.ON.name().equals(deviceCatalogItem.getStatus());
//                        if (online != channel.getOnline()) {
//                            channel.setOnline(online);
//                            publisher.publishEvent(new DeviceOnlineEvent(deviceId, deviceCatalogItem.getDeviceId(), online));
//                            if (!online) {
//                                channel.setLeaveTime(LocalDateTime.now());
//                            }
//                        }
//                        updateById(channel);
//                    });
//                }else{
//                    renewalChannel(deviceId);
//                }
//            }else{
//                throw new BizException("未知的事件类型");
//            }
//        }
//    }
//
//    public DevicePreviewSnapshot realTimeSnap(String deviceId, String channelId) throws IOException {
//        Device device = deviceService.getByDeviceId(deviceId).orElseThrow(() -> new BizException("设备不存在"));
//        DeviceChannel channel = baseMapper.selectByDeviceIdAndChannelId(deviceId, channelId);
//        if (channel == null) {
//            throw new BizException("通道不存在");
//        }
//        String rtspUrl;
//        if(device.getProtocolType() == ProtocolTypeEnum.GB28181){
//            String stream = devicePlayService.streamId(InviteTypeEnum.Play, channel.getId().toString());
//            if (!mediaHttpClient.mediaExists(stream)) {
//                return snapShot(deviceId, channelId);
//            }
//            rtspUrl = devicePlayService.snapShotUrl(stream);
//        }else if(device.getProtocolType() == ProtocolTypeEnum.PULL){
//            rtspUrl = device.getStreamUrl();
//        }else if(device.getProtocolType() == ProtocolTypeEnum.RTMP){
//            rtspUrl = devicePlayService.getRtspPlayUrl(channel.getDeviceId(), channel.getChannelId(), channel.getId().toString(), InviteTypeEnum.Rtmp);
//        }else{
//            log.info("设备不支持拉流，无法截图，deviceId: {}, protocolType: {}", device.getDeviceId(), device.getProtocolType().getText());
//            throw new BizException("设备不支持拉流，无法截图");
//        }
//        try {
//            SnapshotReq req = new SnapshotReq();
//            req.setUrl(rtspUrl);
//            req.setTimeoutSec(2);
//            req.setExpireSec(5);
//            long startTime = System.currentTimeMillis();
//            byte[] snap = mediaHttpClient.getSnap(req);
//            long endTime = System.currentTimeMillis();
//            log.info("截图成功，耗时：{}ms，deviceId: {}, channelId: {}, rtspUrl: {}", (endTime - startTime), deviceId, channelId, rtspUrl);
//            String snapPath = sipConfigProperties.getMedia().getSnapPath();
//            if (!Files.exists(Paths.get(snapPath))) {
//                Files.createDirectories(Paths.get(snapPath));
//            }
//            Files.write(Paths.get(snapPath, channel.getDeviceId() + "_" + channel.getChannelId() + ".jpg"), snap);
//            DevicePreviewSnapshot devicePreviewSnapshot = new DevicePreviewSnapshot();
//            devicePreviewSnapshot.setBase64(Base64.getEncoder().encodeToString(snap));
//            devicePreviewSnapshot.setCreateTime(LocalDateTime.now());
//            return devicePreviewSnapshot;
//        } catch (Exception e) {
//            log.error("截图失败，{}", e);
//        }
//        return snapShot(deviceId, channelId);
//    }
}
