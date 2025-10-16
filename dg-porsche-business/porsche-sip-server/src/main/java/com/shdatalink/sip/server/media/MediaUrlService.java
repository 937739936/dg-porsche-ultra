package com.shdatalink.sip.server.media;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.framework.common.utils.DateUtil;
import com.shdatalink.sip.server.config.SipConfigProperties;
import com.shdatalink.sip.server.gb28181.StreamFactory;
import com.shdatalink.sip.server.gb28181.core.bean.constants.InviteTypeEnum;
import com.shdatalink.sip.server.gb28181.core.bean.constants.TransportTypeEnum;
import com.shdatalink.sip.server.media.bean.entity.req.AddStreamProxyReq;
import com.shdatalink.sip.server.media.bean.entity.req.MediaReq;
import com.shdatalink.sip.server.media.bean.entity.resp.MediaServerResponse;
import com.shdatalink.sip.server.media.bean.entity.resp.ServerNodeConfigResult;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.service.DeviceChannelService;
import com.shdatalink.sip.server.module.device.service.DeviceService;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewPlayVO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.RandomUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static com.shdatalink.sip.server.gb28181.core.bean.constants.SipConstant.SNAPSHOT_TOKEN;

@ApplicationScoped
public class MediaUrlService {
    @Inject
    MediaHttpClient mediaHttpClient;
    @Inject
    SipConfigProperties sipConfigProperties;
    @Inject
    DeviceChannelService deviceChannelService;
    @Inject
    DeviceService deviceService;
    @Inject
    MediaUrlService mediaUrlService;

    public ServerNodeConfigResult getNodeConfig() {
        MediaServerResponse<List<ServerNodeConfigResult>> serverConfigs = mediaHttpClient.getServerConfig();
        if (serverConfigs.getCode() != 0 || serverConfigs.getData() == null || serverConfigs.getData().isEmpty()) {
            throw new BizException("媒体配置获取失败");
        }
        return serverConfigs.getData().getFirst();
    }

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
        ServerNodeConfigResult serverConfig = mediaUrlService.getNodeConfig();
        String mediaIp = sipConfigProperties.media().ip();

        return String.format("rtsp://%s:%d/rtp/%s?token=%s", mediaIp, serverConfig.getRtspPort(), stream, SNAPSHOT_TOKEN);
    }



    public DevicePreviewPlayVO generatePlayUrl(String deviceId, String channelId, String stream, InviteTypeEnum type, boolean random, String ext) {
        if (deviceId == null || channelId == null) {
            return null;
        }

        if (random) {
            stream = StreamFactory.streamId(type, RandomUtils.insecure().randomInt(0,99), stream);
        } else {
            stream = StreamFactory.streamId(type, stream);
        }
        String sign = sign(stream);

        return buildPlayUrl(deviceId, channelId, stream, sign, ext);
    }


    public String buildRtmpStreamUrl(String stream) {
        ServerNodeConfigResult serverConfig = mediaUrlService.getNodeConfig();

        String mediaIp = sipConfigProperties.server().wanIp();
        return String.format("rtmp://%s:%d/rtp/%s", mediaIp, serverConfig.getRtmpPort(), stream);
    }

    public DevicePreviewPlayVO buildPlayUrl(String deviceId, String channelId, String stream, String sign, String ext) {
        String mediaIp = sipConfigProperties.server().wanIp();
        int mediaPort = sipConfigProperties.media().port();
        ServerNodeConfigResult serverConfig = mediaUrlService.getNodeConfig();

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
        String streamId = StreamFactory.streamId(type, stream);
        String sign = sign(streamId);

        String mediaIp = sipConfigProperties.server().wanIp();
        ServerNodeConfigResult serverConfig = mediaUrlService.getNodeConfig();

        return String.format("rtsp://%s:%d/rtp/%s?token=%s%s", mediaIp, serverConfig.getRtspPort(), streamId, sign, "");
    }


    public String sign(String stream) {
        String secret = sipConfigProperties.media().secret();

        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer(sipConfigProperties.server().id())
                .withSubject(stream)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 86400)) // 5分钟有效期
                .sign(algorithm);
    }

    public boolean verify(String streamId, String token) {
        String secret = sipConfigProperties.media().secret();
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier build = JWT.require(algorithm)
                .withIssuer(sipConfigProperties.server().id())
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
        AddStreamProxyReq streamProxyItem = MediaReq.getRtpInstance(streamId, AddStreamProxyReq.class);
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
        DeviceChannel channel = deviceChannelService.getById(StreamFactory.extractChannel(streamId));
        if (channel == null) {
            throw new BizException("通道不存在");
        }
        Device device = deviceService.getByDeviceId(channel.getDeviceId()).orElseThrow(() -> new BizException("设备不存在"));
        addPullStream(device.getStreamUrl(), streamId, TransportTypeEnum.TCP, device.getEnableAudio());
    }
}
