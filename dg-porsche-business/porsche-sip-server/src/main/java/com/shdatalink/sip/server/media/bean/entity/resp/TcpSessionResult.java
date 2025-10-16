package com.shdatalink.sip.server.media.bean.entity.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Class representing the input structure.
 */
@Data
public class TcpSessionResult {
    /**
     * 该tcp链接唯一id
     */
    @JsonProperty("id")
    public String id;
    /**
     * 本机网卡ip
     */
    @JsonProperty("local_ip")
    public String localIp;
    /**
     * 本机端口号 (这是个rtmp播放器或推流器)
     */
    @JsonProperty("local_port")
    public int localPort;
    /**
     * 客户端ip
     */
    @JsonProperty("peer_ip")
    public String peerIp;
    /**
     * 客户端端口号
     */
    @JsonProperty("peer_port")
    public int peerPort;
    /**
     * 客户端TCPSession typeid
     */
    @JsonProperty("typeid")
    public String typeId;
}
