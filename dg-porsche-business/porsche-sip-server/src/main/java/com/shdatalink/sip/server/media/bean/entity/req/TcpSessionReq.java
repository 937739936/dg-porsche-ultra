package com.shdatalink.sip.server.media.bean.entity.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.ws.rs.QueryParam;
import lombok.Data;

@Data
public class TcpSessionReq {
    /**
     * 筛选本机端口，例如筛选 rtsp 链接：554
     */
    @JsonProperty("local_port")
    @QueryParam("local_port")
    private Integer localPort;
    /**
     * 筛选客户端 ip
     */
    @JsonProperty("peer_ip")
    @QueryParam("peer_ip")
    private String peerIp;
}
