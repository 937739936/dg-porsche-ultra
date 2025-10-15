package com.shdatalink.sip.server.module.device.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.framework.common.utils.DateUtil;
import com.shdatalink.sip.server.config.SipConfigProperties;
import com.shdatalink.sip.server.gb28181.core.bean.constants.InviteTypeEnum;
import com.shdatalink.sip.server.gb28181.core.bean.constants.TransportTypeEnum;
import com.shdatalink.sip.server.media.MediaHttpClient;
import com.shdatalink.sip.server.media.bean.entity.ServerNodeConfig;
import com.shdatalink.sip.server.media.bean.entity.StreamProxyItem;
import com.shdatalink.sip.server.media.bean.entity.req.MediaReq;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewPlayVO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static com.shdatalink.sip.server.gb28181.core.bean.constants.SipConstant.SNAPSHOT_TOKEN;

@Slf4j
@ApplicationScoped
public class DevicePlayService {

    @Inject
    MediaHttpClient mediaHttpClient;
    @Inject
    SipConfigProperties sipConfigProperties;
    @Inject
    DeviceChannelService deviceChannelService;
    @Inject
    DeviceService deviceService;

    public DevicePreviewPlayVO playUrl(String deviceId, String channelId, String stream) {
        return generatePlayUrl(deviceId, channelId, stream, InviteTypeEnum.Play, false, "");
    }

    public DevicePreviewPlayVO playPullStreamUrl(String deviceId, String channelId, String stream) {
        return generatePlayUrl(deviceId, channelId, stream, InviteTypeEnum.PullStream, false,"");
    }

    public DevicePreviewPlayVO playRtmpStreamUrl(String deviceId, String channelId, String stream) {
        return generatePlayUrl(deviceId, channelId, stream, InviteTypeEnum.Rtmp, false,"");
    }

    public DevicePreviewPlayVO playBackUrl(String deviceId, String channelId, String stream, LocalDateTime start) {
        LocalDateTime end = start.toLocalDate().atTime(LocalTime.of(23,59,59));
        String startStr = start.format(DateTimeFormatter.ofPattern(DateUtil.DATE_TIME_PATTERN));
        String endStr = end.format(DateTimeFormatter.ofPattern(DateUtil.DATE_TIME_PATTERN));
        String ext = "&start=" + URLEncoder.encode(startStr, StandardCharsets.UTF_8) + "&end=" + URLEncoder.encode(endStr, StandardCharsets.UTF_8);
        return generatePlayUrl(deviceId, channelId, stream, InviteTypeEnum.Playback, true, ext);
    }

    public String snapShotUrl(String stream) {
        List<ServerNodeConfig> serverConfigs = mediaHttpClient.getServerConfig();
        if (serverConfigs == null || serverConfigs.isEmpty()) {
            log.error("media服务器配置获取失败");
            return null;
        }
        String mediaIp = sipConfigProperties.getMedia().getIp();
        ServerNodeConfig serverConfig = serverConfigs.get(0);

        return String.format("rtsp://%s:%d/rtp/%s?token=%s", mediaIp, serverConfig.getRtspPort(), stream, SNAPSHOT_TOKEN);
    }

    public String streamId(InviteTypeEnum action, String stream) {
        return streamId(action, 0, stream);
    }

    public String streamId(InviteTypeEnum action, Integer seq, String stream) {
        return action.getPrefix()+String.format("%02d%06d", seq, Integer.parseInt(stream));
    }

    public Integer extractStream(String stream) {
        return Integer.parseInt(stream.substring(4));
    }

    public DevicePreviewPlayVO generatePlayUrl(String deviceId, String channelId, String stream, InviteTypeEnum type, boolean random, String ext) {
        if (deviceId == null || channelId == null) {
            return null;
        }

        if (random) {
            stream = streamId(type, RandomUtils.insecure().randomInt(0,99), stream);
        } else {
            stream = streamId(type, stream);
        }
        String sign = sign(stream);

        return buildPlayUrl(deviceId, channelId, stream, sign, ext);
    }


    public String buildRtmpStreamUrl(String stream) {
        List<ServerNodeConfig> serverConfigs = mediaHttpClient.getServerConfig();
        if (serverConfigs == null || serverConfigs.isEmpty()) {
            log.error("media服务器配置获取失败");
            return null;
        }
        String mediaIp = sipConfigProperties.getServer().getWanIp();
        ServerNodeConfig serverConfig = serverConfigs.get(0);
        return String.format("rtmp://%s:%d/rtp/%s", mediaIp, serverConfig.getRtmpPort(), stream);
    }

    public DevicePreviewPlayVO buildPlayUrl(String deviceId, String channelId, String stream, String sign, String ext) {
        List<ServerNodeConfig> serverConfigs = mediaHttpClient.getServerConfig();
        if (serverConfigs == null || serverConfigs.isEmpty()) {
            log.error("media服务器配置获取失败");
            return null;
        }
        String mediaIp = sipConfigProperties.getServer().getWanIp();
        int mediaPort = sipConfigProperties.getMedia().getPort();
        ServerNodeConfig serverConfig = serverConfigs.get(0);

        DevicePreviewPlayVO vo = new DevicePreviewPlayVO();
        vo.setDeviceId(deviceId);
        vo.setChannelId(channelId);
        vo.setSsrc(stream);
        vo.setRtspUrl(String.format("rtsp://%s:%d/rtp/%s?token=%s%s", mediaIp, serverConfig.getRtspPort(), stream, sign, ext));
        vo.setRtmpUrl(String.format("rtmp://%s:%d/rtp/%s?token=%s%s", mediaIp, serverConfig.getRtmpPort(), stream, sign, ext));
        vo.setFlvUrl(String.format("http://%s:%d/rtp/%s.live.flv?token=%s%s", mediaIp, mediaPort, stream, sign, ext));
        vo.setWsUrl(String.format("ws://%s:%d/rtp/%s.live.flv?token=%s%s", mediaIp, mediaPort, stream, sign, ext));
        vo.setHlsUrl(String.format("http://%s:%d/rtp/%s/hls.m3u8?token=%s%s", mediaIp, mediaPort, stream, sign, ext));
        vo.setHlsMp4Url(String.format("http://%s:%d/rtp/%s/hls.fmp4.m3u8?token=%s%s", mediaIp, mediaPort, stream, sign, ext));
        vo.setHttpTsUrl(String.format("http://%s:%d/rtp/%s.live.ts?token=%s%s", mediaIp, mediaPort, stream, sign, ext));
        vo.setWsTsUrl(String.format("ws://%s:%d/rtp/%s.live.ts?token=%s%s", mediaIp, mediaPort, stream, sign, ext));
        vo.setHttpMp4Url(String.format("http://%s:%d/rtp/%s.live.mp4?token=%s%s", mediaIp, mediaPort, stream, sign, ext));
        vo.setWsMp4Url(String.format("ws://%s:%d/rtp/%s.live.mp4?token=%s%s", mediaIp, mediaPort, stream, sign, ext));
        vo.setWebRtcUrl(String.format("webrtc://%s:%d/index/api/webrtc?app=rtp&stream=%s&type=play&token=%s%s", mediaIp, mediaPort, stream, sign, ext));
        return vo;
    }


    public String getRtspPlayUrl(String deviceId, String channelId, String stream, InviteTypeEnum type) {
        if (deviceId == null || channelId == null) {
            return null;
        }
        String streamId = streamId(type, stream);
        String sign = sign(streamId);
        List<ServerNodeConfig> serverConfigs = mediaHttpClient.getServerConfig();
        if (serverConfigs == null || serverConfigs.isEmpty()) {
            log.error("media服务器配置获取失败");
            return null;
        }
        String mediaIp = sipConfigProperties.getServer().getWanIp();
        ServerNodeConfig serverConfig = serverConfigs.get(0);
        return String.format("rtsp://%s:%d/rtp/%s?token=%s%s", mediaIp, serverConfig.getRtspPort(), streamId, sign, "");
    }


    public String sign(String stream) {
        String secret = sipConfigProperties.getMedia().getSecret();

        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer(sipConfigProperties.getServer().getId())
                .withSubject(stream)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 86400)) // 5分钟有效期
                .sign(algorithm);
    }

    public boolean verify(String streamId, String token) {
        String secret = sipConfigProperties.getMedia().getSecret();
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier build = JWT.require(algorithm)
                .withIssuer(sipConfigProperties.getServer().getId())
                .withSubject(streamId)
                .build();
        try {
            DecodedJWT decodedJWT = build.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public Boolean addPullStream(String url, String streamId, TransportTypeEnum transportType, Boolean enableAudio) {
        StreamProxyItem streamProxyItem = MediaReq.getRtpInstance(streamId, StreamProxyItem.class);
        streamProxyItem.setUrl(url);
        streamProxyItem.setStream(streamId);
        streamProxyItem.setRtpType(transportType == TransportTypeEnum.TCP ? 0 : 1);
        streamProxyItem.setAutoClose(true);
        streamProxyItem.setEnableAudio(enableAudio);
        streamProxyItem.setAddMuteAudio(!enableAudio);
        try {
            mediaHttpClient.addStreamProxy(streamProxyItem);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void playPullStream(String streamId) {
        DeviceChannel channel = deviceChannelService.getById(extractStream(streamId));
        if (channel == null) {
            throw new BizException("通道不存在");
        }
        Device device = deviceService.getByDeviceId(channel.getDeviceId()).orElseThrow(() -> new BizException("设备不存在"));
        addPullStream(device.getStreamUrl(), streamId, TransportTypeEnum.TCP, device.getEnableAudio());
    }
}
