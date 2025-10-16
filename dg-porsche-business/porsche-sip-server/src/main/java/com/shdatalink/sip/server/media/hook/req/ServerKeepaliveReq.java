package com.shdatalink.sip.server.media.hook.req;

import lombok.Data;

@Data
public class ServerKeepaliveReq {

    /**
     * 资源使用详情
     */
    private ResourceData data;

    /**
     * 服务器ID，标识具体的媒体服务器
     */
    private String mediaServerId;

    /**
     * 资源数据内部类，包含各种资源的使用数量
     */
    @Data
    public static class ResourceData {
        /**
         * 缓冲区数量
         */
        private Integer Buffer;

        /**
         * 类似字符串的缓冲区数量
         */
        private Integer BufferLikeString;

        /**
         * 缓冲区列表数量
         */
        private Integer BufferList;

        /**
         * 原始缓冲区数量
         */
        private Integer BufferRaw;

        /**
         * 帧数量
         */
        private Integer Frame;

        /**
         * 帧实现数量
         */
        private Integer FrameImp;

        /**
         * 媒体源数量
         */
        private Integer MediaSource;

        /**
         * 多媒体源复用器数量
         */
        private Integer MultiMediaSourceMuxer;

        /**
         * RTMP 数据包数量
         */
        private Integer RtmpPacket;

        /**
         * RTP 数据包数量
         */
        private Integer RtpPacket;

        /**
         * 套接字数量
         */
        private Integer Socket;

        /**
         * TCP 客户端数量
         */
        private Integer TcpClient;

        /**
         * TCP 服务器数量
         */
        private Integer TcpServer;

        /**
         * TCP 会话数量
         */
        private Integer TcpSession;

        /**
         * UDP 服务器数量
         */
        private Integer UdpServer;

        /**
         * UDP 会话数量
         */
        private Integer UdpSession;
    }
}