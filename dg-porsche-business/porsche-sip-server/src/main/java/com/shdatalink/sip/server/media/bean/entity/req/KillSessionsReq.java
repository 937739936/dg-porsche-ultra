package com.shdatalink.sip.server.media.bean.entity.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KillSessionsReq {

    @JsonProperty("local_port")
    private String localPort;

    @JsonProperty("peer_ip")
    private String peerIp;
}
