package com.shdatalink.sip.server.module.device.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;

import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.framework.redis.utils.RedisUtil;
import com.shdatalink.sip.server.config.SipConfigProperties;
import com.shdatalink.sip.server.gb28181.StreamFactory;
import com.shdatalink.sip.server.gb28181.core.bean.constants.InviteTypeEnum;
import com.shdatalink.sip.server.media.MediaHttpClient;
import com.shdatalink.sip.server.media.MediaService;
import com.shdatalink.sip.server.media.bean.entity.req.SnapshotReq;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewSnapshot;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.device.service.DeviceSnapService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@Slf4j
@ApplicationScoped
public class DeviceSnapService {

    @Inject
    @RestClient
    MediaHttpClient mediaHttpClient;
    @Inject
    SipConfigProperties sipConfigProperties;
    @Inject
    DeviceService deviceService;
    @Inject
    DeviceChannelService deviceChannelService;
    @Inject
    MediaService mediaService;
    private static final Map<String, Object> LOCK_MAP = new ConcurrentHashMap<>();

    public void updateDeviceSnap(Device device, DeviceChannel channel) {
        if (device.getProtocolType() == ProtocolTypeEnum.PULL) {
            SnapshotReq req = new SnapshotReq();
            req.setUrl(device.getStreamUrl());
            req.setTimeoutSec(30);
            req.setExpireSec(60);
            byte[] snap = mediaHttpClient.getSnap(req);
            String snapPath = sipConfigProperties.media().snapPath();;
            if (!Files.exists(Paths.get(snapPath))) {
                try {
                    Files.createDirectories(Paths.get(snapPath));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                Files.write(Paths.get(snapPath, channel.getDeviceId() + "_" + channel.getChannelId() + ".jpg"), snap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public DevicePreviewSnapshot realTimeSnap(String deviceId, String channelId) throws IOException {
        Device device = deviceService.getByDeviceId(deviceId).orElseThrow(() -> new BizException("设备不存在"));
        DeviceChannel channel = deviceChannelService.findByDeviceIdAndChannelId(deviceId, channelId).orElseThrow(() -> new BizException("通道不存在"));
        String rtspUrl = mediaService.getSnapshotUrl(device, channel.getId());
        if(device.getProtocolType() == ProtocolTypeEnum.GB28181){
            String stream = StreamFactory.streamId(InviteTypeEnum.Play, channel.getId().toString());
            if (!mediaService.mediaExists(stream)) {
                return querySnapshot(deviceId, channelId);
            }
        }
        Object obj = LOCK_MAP.computeIfAbsent(rtspUrl, k -> new Object());
        String snapPath = sipConfigProperties.media().snapPath();
        Path path = Paths.get(snapPath, channel.getDeviceId() + "_" + channel.getChannelId() + ".jpg");
        try {
            synchronized (obj) {
                SnapshotReq req = new SnapshotReq();
                req.setUrl(rtspUrl);
                req.setTimeoutSec(7);
                req.setExpireSec(10);
                long startTime = System.currentTimeMillis();
                byte[] snap = mediaHttpClient.getSnap(req);
                long endTime = System.currentTimeMillis();
                log.info("截图成功，耗时：{}ms，deviceId: {}, channelId: {}, rtspUrl: {}", (endTime - startTime), deviceId, channelId, rtspUrl);
                if (!Files.exists(Paths.get(snapPath))) {
                    Files.createDirectories(Paths.get(snapPath));
                }
                Files.write(path, snap);
            }
            DevicePreviewSnapshot devicePreviewSnapshot = new DevicePreviewSnapshot();
            if (Files.exists(path)) {
                devicePreviewSnapshot.setBase64(Base64.getEncoder().encodeToString(Files.readAllBytes(path)));
            }
            devicePreviewSnapshot.setCreateTime(LocalDateTime.now());
            return devicePreviewSnapshot;
        } catch (Exception e) {
            log.error("截图失败，{}", e);
        }
        return querySnapshot(deviceId, channelId);
    }


    public DevicePreviewSnapshot querySnapshot(String deviceId, String channelId) throws IOException {
        DevicePreviewSnapshot snapshot = new DevicePreviewSnapshot();
        String stream = deviceId+"_"+channelId;
        String snapPath = sipConfigProperties.media().snapPath();
        Path path = Paths.get(snapPath, stream + ".jpg");
        if (Files.exists(path)) {
            byte[] bytes = Files.readAllBytes(path);
            snapshot.setBase64(Base64.getEncoder().encodeToString(bytes));
            BasicFileAttributes basicFileAttributes = Files.readAttributes(path, BasicFileAttributes.class);
            if (basicFileAttributes != null && basicFileAttributes.isRegularFile()) {
                snapshot.setCreateTime(basicFileAttributes.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            }
        }
        return snapshot;
    }
}
