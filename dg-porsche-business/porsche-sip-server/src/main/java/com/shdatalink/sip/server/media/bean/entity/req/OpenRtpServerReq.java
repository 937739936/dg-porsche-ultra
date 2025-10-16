package com.shdatalink.sip.server.media.bean.entity.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenRtpServerReq {

    /**
     * 接收端口，0则为随机端口。
     */
    private int port;

    /**
     * 0 udp 模式，1 tcp 被动模式, 2 tcp 主动模式。 (兼容enable_tcp 为0/1)。
     */
    @JsonProperty("tcp_mode")
    private int tcpMode;

    /**
     * 该端口绑定的流ID，该端口只能创建这一个流(而不是根据ssrc创建多个)。
     */
    @JsonProperty("stream_id")
    private String streamId;


    @JsonProperty("enable_tcp")
    private boolean enableTcp = true;

}
