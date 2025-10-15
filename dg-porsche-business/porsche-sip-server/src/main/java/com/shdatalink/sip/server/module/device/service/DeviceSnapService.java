package com.shdatalink.sip.server.module.device.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.device.service.DeviceSnapService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@Slf4j
@ApplicationScoped
public class DeviceSnapService {

//    @Inject
//    MediaHttpClient mediaHttpClient;
//    @Inject
//    SipConfigProperties sipConfigProperties;
//
//    public void updateDeviceSnap(Device device, DeviceChannel channel) {
//        if (device.getProtocolType() == ProtocolTypeEnum.PULL) {
//            SnapshotReq req = new SnapshotReq();
//            req.setUrl(device.getStreamUrl());
//            req.setTimeoutSec(30);
//            req.setExpireSec(60);
//            byte[] snap = mediaHttpClient.getSnap(req);
//            String snapPath = sipConfigProperties.getMedia().getSnapPath();
//            if (!Files.exists(Paths.get(snapPath))) {
//                try {
//                    Files.createDirectories(Paths.get(snapPath));
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            try {
//                Files.write(Paths.get(snapPath, channel.getDeviceId() + "_" + channel.getChannelId() + ".jpg"), snap);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }

}
