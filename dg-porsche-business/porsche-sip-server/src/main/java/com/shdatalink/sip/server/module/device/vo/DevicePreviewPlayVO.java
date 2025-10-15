package com.shdatalink.sip.server.module.device.vo;

import lombok.Data;

@Data
public class DevicePreviewPlayVO {
    private String deviceId;
    private String channelId;
    private String ssrc;
    /**
     * rtsp播放地址
     */
    private String rtspUrl;
    private String rtmpUrl;
    private String flvUrl;
    private String wsUrl;
    private String hlsUrl;
    private String hlsMp4Url;
    private String httpTsUrl;
    private String wsTsUrl;
    private String httpMp4Url;
    private String wsMp4Url;
    private String webRtcUrl;
}
