package com.shdatalink.sip.server.media.hook.req;

import lombok.Data;

@Data
public class StreamNoneReaderReq {

    /**
     * 流应用名，标识特定的应用或业务场景
     */
    private String app;

    /**
     * 流协议类型，固定为 "rtsp" 或 "rtmp"
     */
    private String schema;

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