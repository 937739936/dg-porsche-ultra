package com.shdatalink.sip.server.media.hook.req;

import lombok.Data;

@Data
public class RtspRealmReq {

    /**
     * 流应用名，标识特定的应用或业务场景
     */
    private String app;

    /**
     * TCP 连接唯一 ID，用于标识一次连接会话
     */
    private String id;

    /**
     * RTSP 播放器 IP 地址
     */
    private String ip;

    /**
     * 播放 RTSP URL 的参数部分，包含额外配置信息
     */
    private String params;

    /**
     * RTSP 播放器端口号（使用 int 类型兼容无符号短整型）
     */
    private Integer port;

    /**
     * 媒体源类型，固定为 "rtsp"
     */
    private String schema;

    /**
     * 传输协议，如 rtsp/rtsps/udp/tcp 等
     */
    private String protocol;

    /**
     * 流 ID，唯一标识一个媒体流
     */
    private String stream;

    /**
     * 流虚拟主机，用于逻辑隔离不同租户或域名
     */
    private String vhost;

    /**
     * 服务器 ID，通过配置文件设置，用于区分不同服务器
     */
    private String mediaServerId;
}