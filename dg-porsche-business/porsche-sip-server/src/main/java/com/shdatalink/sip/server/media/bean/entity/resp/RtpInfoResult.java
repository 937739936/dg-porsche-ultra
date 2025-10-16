package com.shdatalink.sip.server.media.bean.entity.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 获取rtp代理时的某路ssrc rtp信息。
 */
@Data
public class RtpInfoResult {

    /**
     * 状态码。
     */
    @JsonProperty("code")
    private int code;

    /**
     * 会话是否存在。
     */
    @JsonProperty("exist")
    private boolean exist;

    /**
     * 推流客户端ip。
     */
    @JsonProperty("peer_ip")
    private String peerIp;

    /**
     * 客户端端口号。
     */
    @JsonProperty("peer_port")
    private int peerPort;

    /**
     * 本地监听的网卡ip。
     */
    @JsonProperty("local_ip")
    private String localIp;

    /**
     * 本地端口号。
     */
    @JsonProperty("local_port")
    private int localPort;

}
