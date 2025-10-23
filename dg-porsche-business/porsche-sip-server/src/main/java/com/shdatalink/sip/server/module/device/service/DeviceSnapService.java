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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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

    public void snapshot(String deviceId, String channelId) {
        Device device = deviceService.getByDeviceId(deviceId).orElseThrow(() -> new BizException("设备不存在"));
        DeviceChannel channel = deviceChannelService.findByDeviceIdAndChannelId(deviceId, channelId).orElseThrow(() -> new BizException("通道不存在"));
        snapshot(device, channel);
    }

    public void snapshot(String deviceId, String channelId, Path snapPath) {
        Device device = deviceService.getByDeviceId(deviceId).orElseThrow(() -> new BizException("设备不存在"));
        DeviceChannel channel = deviceChannelService.findByDeviceIdAndChannelId(deviceId, channelId).orElseThrow(() -> new BizException("通道不存在"));
        snapshot(device, channel, snapPath);
    }

    public void snapshot(Device device, DeviceChannel channel) {
        String rtspUrl = mediaService.getSnapshotUrl(device, channel.getId());
        SnapshotReq req = new SnapshotReq();
        req.setUrl(rtspUrl);
        req.setTimeoutSec(30);
        req.setExpireSec(60);
        snapshot(req, channel.getDeviceId(), channel.getChannelId());
    }

    public void snapshot(Device device, DeviceChannel channel, Path snapshotPath) {
        String rtspUrl = mediaService.getSnapshotUrl(device, channel.getId());
        SnapshotReq req = new SnapshotReq();
        req.setUrl(rtspUrl);
        req.setTimeoutSec(30);
        req.setExpireSec(60);
        snapshot(req, snapshotPath, channel.getDeviceId(), channel.getChannelId());
    }

    public Path getSnapPath(String deviceId, String channelId) {
        String snapPath = sipConfigProperties.media().snapPath();
        return Paths.get(snapPath, deviceId + "_" + channelId + ".jpg");
    }

    public Path getPreviewPath(String deviceId, String channelId) {
        String snapPath = sipConfigProperties.media().snapPath();
        return Paths.get(snapPath, "preview", deviceId + "_" + channelId + ".jpg");
    }

    public Path getPreviewPathFromOrigin(Path originPath) {
        return originPath.getParent().resolve("preview").resolve(originPath.getFileName());
    }

    public byte[] snapshot(SnapshotReq req, String deviceId, String channelId) {
        return snapshot(req, getSnapPath(deviceId, channelId), deviceId, channelId);
    }

    public byte[] snapshot(SnapshotReq req, Path snapSavePath, String deviceId, String channelId) {
        Object obj = LOCK_MAP.computeIfAbsent(req.getUrl(), k -> new Object());
        synchronized (obj) {
            long startTime = System.currentTimeMillis();
//
            try {
                byte[] snap = mediaHttpClient.getSnap(req);
                if (snap[0] == '{') {
                    // 返回的json
                    throw new RuntimeException("截图失败, media server 返回" + new String(snap));
                }
                long endTime = System.currentTimeMillis();
                log.info("截图成功，耗时：{}ms，deviceId: {}, channelId: {}, rtspUrl: {}", (endTime - startTime), deviceId, channelId, req.getUrl());
                Path dir = Paths.get(snapSavePath.getParent().toString());
                if (!Files.exists(dir)) {
                    Files.createDirectories(dir);
                }
                Files.write(snapSavePath, snap);
                generatePreview(snap, 80, getPreviewPathFromOrigin(snapSavePath));
                return snap;
            } catch (Exception e) {
                log.error("截图失败，{}", e.getMessage());
            }
        }
        return null;
    }

    private void generatePreview(byte[] snap, int width, Path path) {
        try {
            if (!path.getParent().toFile().exists()) {
                path.getParent().toFile().mkdirs();
            }
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(snap));
            float originWidth = originalImage.getWidth();
            float originHeight = originalImage.getHeight();
            int height = (int) (width / originWidth * originHeight);
            BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = scaledImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(originalImage, 0, 0, width, height, null);
            g2d.dispose();
            ImageIO.write(scaledImage, "jpg", path.toFile());
            log.info("缩略图已生成：{}", path);
        } catch (IOException e) {
            log.error("缩略图生成失败", e);
        }
    }


    public DevicePreviewSnapshot realTimeSnap(String channelId) {
        DeviceChannel channel = deviceChannelService.getBaseMapper().selectByChannelId(channelId);
        if (channel == null) {
            throw new BizException("通道不存在");
        }
        Device device = deviceService.getByDeviceId(channel.getDeviceId()).orElseThrow(() -> new BizException("设备不存在"));
        return realTimeSnap(device, channel);
    }

    public DevicePreviewSnapshot realTimeSnap(String deviceId, String channelId) {
        Device device = deviceService.getByDeviceId(deviceId).orElseThrow(() -> new BizException("设备不存在"));
        DeviceChannel channel = deviceChannelService.findByDeviceIdAndChannelId(deviceId, channelId).orElseThrow(() -> new BizException("通道不存在"));
        return realTimeSnap(device, channel);
    }
    public DevicePreviewSnapshot realTimeSnap(Device device, DeviceChannel channel) {
        String rtspUrl = mediaService.getSnapshotUrl(device, channel.getId());
        if(device.getProtocolType() == ProtocolTypeEnum.GB28181){
            String stream = StreamFactory.streamId(InviteTypeEnum.Play, channel.getId().toString());
            if (!mediaService.mediaExists(stream)) {
                return querySnapshot(device.getDeviceId(), channel.getChannelId());
            }
        }
        SnapshotReq req = new SnapshotReq();
        req.setUrl(rtspUrl);
        req.setTimeoutSec(7);
        req.setExpireSec(10);
        byte[] snapshot = snapshot(req, device.getDeviceId(), channel.getChannelId());
        if (snapshot == null) {
            return querySnapshot(device.getDeviceId(), channel.getChannelId());
        }

        generatePreview(snapshot, 120, getPreviewPath(channel.getDeviceId(), channel.getChannelId()));
        DevicePreviewSnapshot devicePreviewSnapshot = new DevicePreviewSnapshot();
        devicePreviewSnapshot.setBase64(Base64.getEncoder().encodeToString(snapshot));
        devicePreviewSnapshot.setCreateTime(LocalDateTime.now());
        return devicePreviewSnapshot;
    }

    public DevicePreviewSnapshot queryPreview(String deviceId, String channelId) {
        Path path = getPreviewPath(deviceId, channelId);
        return querySnapshot(path);
    }

    public DevicePreviewSnapshot querySnapshot(String deviceId, String channelId) {
        Path path = getSnapPath(deviceId, channelId);
        return querySnapshot(path);
    }
    public DevicePreviewSnapshot querySnapshot(Path path) {
        DevicePreviewSnapshot snapshot = new DevicePreviewSnapshot();
        try {
            if (Files.exists(path)) {
                byte[] bytes = Files.readAllBytes(path);
                snapshot.setBase64(Base64.getEncoder().encodeToString(bytes));
                BasicFileAttributes basicFileAttributes = Files.readAttributes(path, BasicFileAttributes.class);
                if (basicFileAttributes != null && basicFileAttributes.isRegularFile()) {
                    snapshot.setCreateTime(basicFileAttributes.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                }
            }
        } catch (IOException e) {
            log.error("截图读取失败", e);
        }
        return snapshot;
    }
}
