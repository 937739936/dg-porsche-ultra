package com.shdatalink.sip.server.integration.vo;

import lombok.Data;

@Data
public class IntegrationDevicePreviewPlayVO {
    private String deviceId;
    private String channelId;
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