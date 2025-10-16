package com.shdatalink.sip.server.media.bean.entity.req;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 作为GB28181客户端，启动ps-rtp推流，支持rtp/udp方式；该接口支持rtsp/rtmp等协议转ps-rtp推流。
 * 第一次推流失败会直接返回错误，成功一次后，后续失败也将无限重试。。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StartSendRtpReq extends MediaReq {

    /**
     * 推流的rtp的ssrc,指定不同的ssrc可以同时推流到多个服务器。
     */
    private int ssrc;

    /**
     * 目标ip或域名。
     */
    @JsonProperty("dst_url")
    private String dstUrl;

    /**
     * 目标端口。
     */
    @JsonProperty("dst_port")
    private int dstPort;

    /**
     * 是否为udp模式,否则为tcp模式。
     */
    @JsonProperty("is_udp")
    private boolean isUdp;

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
