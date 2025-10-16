package com.shdatalink.sip.server.media.bean.entity.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StartSendRtpPassiveReq {
    /**
     * 推流的rtp的ssrc,指定不同的ssrc可以同时推流到多个服务器。
     */
    private int ssrc;

    /**
     * 使用的本机端口，为0或不传时默认为随机端口。
     */
    @JsonProperty("src_port")
    private Integer srcPort;

    /**
     * 发送时，rtp的pt（uint8_t）,不传时默认为96。
     */
    private Integer pt;


    /**
     * 发送时，rtp的负载类型。为1时，负载为ps；为0时，为es；不传时默认为1。
     */
    @JsonProperty("use_ps")
    private Integer usePs;

    /**
     * 当use_ps 为0时，有效。为1时，发送音频；为0时，发送视频；不传时默认为0。
     */
    @JsonProperty("only_audio")
    private Boolean onlyAudio;

}
