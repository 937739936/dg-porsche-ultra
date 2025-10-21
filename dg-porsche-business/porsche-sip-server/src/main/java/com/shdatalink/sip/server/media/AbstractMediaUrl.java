package com.shdatalink.sip.server.media;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.shdatalink.framework.common.utils.QuarkusUtil;
import com.shdatalink.sip.server.config.SipConfigProperties;
import com.shdatalink.sip.server.media.bean.entity.resp.ServerNodeConfigResult;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewPlayVO;

import java.time.LocalDateTime;
import java.util.Date;

public abstract class AbstractMediaUrl implements MediaUrl {
    protected final SipConfigProperties sipConfigProperties;
    protected final MediaService mediaService;
    protected final MediaSignService mediaSignService;

    public AbstractMediaUrl() {
        this.sipConfigProperties = QuarkusUtil.getBean(SipConfigProperties.class);
        this.mediaService = QuarkusUtil.getBean(MediaService.class);
        this.mediaSignService = QuarkusUtil.getBean(MediaSignService.class);
    }

    @Override
    public DevicePreviewPlayVO play(Integer channelPrimaryId) {
        throw new RuntimeException("not supported");
    }

    @Override
    public DevicePreviewPlayVO playback(Integer channelPrimaryId, LocalDateTime start) {
        throw new RuntimeException("not supported");
    }

    @Override
    public String snapshot(Integer channelPrimaryId) {
        throw new RuntimeException("not supported");
    }

    protected DevicePreviewPlayVO buildInner(String stream, String sign, String ext) {
        String mediaIp = sipConfigProperties.media().ip();
        int mediaPort = sipConfigProperties.media().port();
        return build(stream, mediaIp, mediaPort, sign, ext);
    }

    protected DevicePreviewPlayVO build(String stream, String sign, String ext) {
        String mediaIp = sipConfigProperties.server().wanIp();
        int mediaPort = sipConfigProperties.media().port();
        return build(stream, mediaIp, mediaPort, sign, ext);
    }
    protected DevicePreviewPlayVO build(String stream, String mediaIp, Integer mediaPort, String sign, String ext) {

        ServerNodeConfigResult serverConfig = mediaService.getFirstServerNodeConfig();

        DevicePreviewPlayVO vo = new DevicePreviewPlayVO();
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
}
