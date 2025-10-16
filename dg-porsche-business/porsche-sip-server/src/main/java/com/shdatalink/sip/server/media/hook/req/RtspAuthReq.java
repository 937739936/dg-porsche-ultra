package com.shdatalink.sip.server.media.hook.req;

import lombok.Data;

@Data
public class RtspAuthReq {

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
     * 请求的密码是否必须为明文标识
     * true: 必须使用明文密码（用于 Base64 鉴权）
     * false: 可以使用加密密码
     */
    private Boolean must_no_encrypt;

    /**
     * RTSP URL 的参数部分，包含额外配置信息
     */
    private String params;

    /**
     * RTSP 播放器端口号（使用 int 类型兼容无符号短整型）
     */
    private Integer port;

    /**
     * RTSP 播放鉴权加密 realm，用于 Digest 认证
     */
    private String realm;

    /**
     * 媒体源类型，固定为 "rtsp"
     */
    private String schema;

    /**
     * 传输协议，固定为 "rtsp" 或 "rtsps"
     */
    private String protocol;

    /**
     * 流 ID，唯一标识一个媒体流
     */
    private String stream;

    /**
     * 播放用户名，用于 RTSP 鉴权
     */
    private String user_name;

    /**
     * 流虚拟主机，用于逻辑隔离不同租户或域名
     */
    private String vhost;

    /**
     * 服务器 ID，通过配置文件设置，用于区分不同服务器
     */
    private String mediaServerId;
}