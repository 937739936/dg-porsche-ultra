package com.shdatalink.sip.server.module.pushstream.vo;

import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PushStreamPageResp {
    /**
     * 设备ID
     */
    private String deviceId;
    /**
     * 通道ID
     */
    private String channelId;
    /**
     * 协议类型
     */
    private ProtocolTypeEnum protocolType;
    /**
     * 流id
     */
    private String streamId;
    /**
     * 推流类型
     */
    private String streamType;
    /**
     * 快照图片的base64
     */
    private String base64;
    /**
     * 推流地址
     */
    private String streamUrl;
    /**
     * 丢包率
     */
    private String packetLossRate;
    /**
     * 比特率
     */
    private int bytesSpeed;
    /**
     * 活跃时间(s)
     */
    private int aliveSecond;
    /**
     * 无人观看(s)
     */
    private Long noViewerSecond;
    /**
     * 状态
     */
    private Boolean online;
    /**
     * 观看人数
     */
    private Integer totalReaderCount;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 视频信息
     */
    private VideoInfo video;

    /**
     * 音频信息
     */
    private AudioInfo audio;


    @Data
    public static class VideoInfo {
        /**
         * 宽
         */
        private Integer width;
        /**
         * 高
         */
        private Integer height;
        /**
         * 帧率
         */
        private Integer fps;
        /**
         * 编码类型
         */
        private String codecName;
    }

    @Data
    public static class AudioInfo {
        /**
         * 音频采样率
         */
        private Integer sampleRate;

        /**
         * 编码类型
         */
        private String codecName;
    }

}
