package com.shdatalink.sip.server.media.hook.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class PublishReq {

    /**
     * 流应用名，标识特定的应用或业务场景
     */
    private String app;

    /**
     * TCP连接唯一ID，用于标识一次连接会话
     */
    private String id;

    /**
     * 推流器IP地址
     */
    private String ip;

    /**
     * 推流URL的参数部分，包含额外配置信息
     */
    private String params;

    /**
     * 推流器端口号（使用int类型兼容无符号短整型）
     */
    private Integer port;

    /**
     * 推流的媒体源类型，如rtsp、rtmp、srt等
     */
    private String schema;

    /**
     * 推流的传输协议，如rtsp/rtmp/rtsps/rtmps/rtc等
     */
    private String protocol;

    /**
     * 流ID，唯一标识一个媒体流
     */
    private String stream;

    /**
     * 流虚拟主机，用于逻辑隔离不同租户或域名
     */
    private String vhost;

    /**
     * 服务器ID，通过配置文件设置，用于区分不同服务器
     */
    private String mediaServerId;
}