package com.shdatalink.sip.server.media.bean.entity.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MediaStatisticsResult {

    @JsonProperty("Buffer")
    private Integer buffer;

    @JsonProperty("RtpPacket")
    private Integer rtpPacket;

    @JsonProperty("Frame")
    private Integer frame;

    @JsonProperty("RtmpPacket")
    private Integer rtmpPacket;

    @JsonProperty("TcpSession")
    private Integer tcpSession;

    @JsonProperty("UdpServer")
    private Integer udpServer;

    @JsonProperty("TcpServer")
    private Integer tcpServer;

    @JsonProperty("FrameImp")
    private Integer frameImp;

    @JsonProperty("BufferList")
    private Integer bufferList;

    @JsonProperty("BufferRaw")
    private Integer bufferRaw;

    @JsonProperty("MediaSource")
    private Integer mediaSource;

    @JsonProperty("MultiMediaSourceMuxer")
    private Integer multiMediaSourceMuxer;

    @JsonProperty("TcpClient")
    private Integer tcpClient;

    @JsonProperty("BufferLikeString")
    private Integer bufferLikeString;

    @JsonProperty("Socket")
    private Integer socket;

    @JsonProperty("UdpSession")
    private Integer udpSession;
}