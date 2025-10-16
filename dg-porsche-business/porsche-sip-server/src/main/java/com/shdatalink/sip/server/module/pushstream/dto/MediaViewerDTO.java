package com.shdatalink.sip.server.module.pushstream.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MediaViewerDTO {

    /**
     * TCP连接唯一ID，用于标识一次连接会话
     */
    private String id;

    /**
     * 流应用名，标识特定的应用或业务场景
     */
    private String app;

    /**
     * 播放器IP地址
     */
    private String ip;

    /**
     * 播放器端口号（使用int类型兼容无符号短整型）
     */
    private Integer port;

    /**
     * 播放的媒体源类型，如rtsp、rtmp、fmp4、ts、hls等
     */
    private String schema;

    /**
     * 播放的传输协议，如rtsp/rtmp/rtsps/rtmps/rtc等
     */
    private String protocol;

    /**
     * 流ID，唯一标识一个媒体流
     */
    private String stream;
    /**
     * 播放时间
     */
    private LocalDateTime playTime;

}
