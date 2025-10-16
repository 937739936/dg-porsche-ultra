package com.shdatalink.sip.server.media.hook.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RtpServerTimeoutReq {

    /**
     * 本地端口号，用于RTP数据传输
     */
    @JsonProperty("local_port")
    private Integer localPort;

    /**
     * 是否启用端口复用
     * true: 允许端口复用
     * false: 不允许端口复用
     */
    @JsonProperty("re_use_port")
    private Boolean reUsePort;

    /**
     * 同步源标识符，32位无符号整数（使用Long类型兼容）
     */
    private Long ssrc;

    /**
     * 流ID，唯一标识一个媒体流
     */
    @JsonProperty("stream_id")
    private String streamId;

    /**
     * TCP模式设置
     * 0: UDP模式
     * 1: TCP被动模式（等待客户端连接）
     * 2: TCP主动模式（主动连接客户端）
     */
    @JsonProperty("tcp_mode")
    private Integer tcpMode;

    /**
     * 服务器ID，通过配置文件设置，用于区分不同服务器
     */
    private String mediaServerId;
}