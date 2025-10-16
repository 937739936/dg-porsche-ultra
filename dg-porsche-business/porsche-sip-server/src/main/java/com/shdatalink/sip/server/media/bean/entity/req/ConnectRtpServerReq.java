package com.shdatalink.sip.server.media.bean.entity.req;

import lombok.Data;


@Data
public class ConnectRtpServerReq {

    /**
     * tcp主动模式时服务端端口
     */
    private int dstPort;

    /**
     * tcp主动模式时服务端地址
     */
    private int dstUrl;

    /**
     * 该端口绑定的流ID，该端口只能创建这一个流(而不是根据ssrc创建多个)。
     */
    private String streamId;

}
