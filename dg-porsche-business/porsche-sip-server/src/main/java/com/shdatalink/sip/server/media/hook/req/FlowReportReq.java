package com.shdatalink.sip.server.media.hook.req;

import lombok.Data;

@Data
public class FlowReportReq {

    /**
     * 流应用名，标识特定的应用或业务场景
     */
    private String app;

    /**
     * TCP连接维持时间，单位为秒
     */
    private Integer duration;

    /**
     * 推流或播放URL的参数部分，包含额外配置信息
     */
    private String params;

    /**
     * 连接角色标识：true表示播放器（接收流），false表示推流器（发送流）
     */
    private Boolean player;

    /**
     * 播放或推流的媒体源类型，如rtsp、rtmp、fmp4、ts、hls等
     */
    private String schema;

    /**
     * 传输协议类型，如http/https/ws/wss/rtsp/rtmp/rtc等
     */
    private String protocol;

    /**
     * 流ID，唯一标识一个媒体流
     */
    private String stream;

    /**
     * 上下行流量总和，单位为字节
     */
    private Integer totalBytes;

    /**
     * 流虚拟主机，用于逻辑隔离不同租户或域名
     */
    private String vhost;

    /**
     * 客户端IP地址
     */
    private String ip;

    /**
     * 客户端端口号
     */
    private Integer port;

    /**
     * TCP连接唯一ID，用于标识一次连接会话
     */
    private String id;

    /**
     * 服务器ID，通过配置文件设置，用于区分不同服务器
     */
    private String mediaServerId;
}