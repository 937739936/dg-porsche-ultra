package com.shdatalink.sip.server.media.bean.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Track {

    /**
     * 累计接收帧数
     */
    @JsonProperty("frames")
    public int frames;
    /**
     * 音频采样位数
     */
    @JsonProperty("sample_bit")
    public int sampleBit;
    /**
     * 音频采样率
     */
    @JsonProperty("sample_rate")
    public int sampleRate;
    /**
     * gop间隔时间，单位毫秒
     */
    @JsonProperty("gop_interval_ms")
    public int gopIntervalMs;
    /**
     * gop大小，单位帧数
     */
    @JsonProperty("gop_size")
    public int gopSzize;
    /**
     * 累计接收关键帧数
     */
    @JsonProperty("key_frames")
    public int keyFrames;
    /**
     * 音频通道数。
     */
    @JsonProperty("channels")
    private int channels;
    /**
     * 编码ID。H264 = 0, H265 = 1, AAC = 2, G711A = 3, G711U = 4。
     */
    @JsonProperty("codec_id")
    private int codecId;
    /**
     * 编码类型的名称。
     */
    @JsonProperty("codec_id_name")
    private String codecIdName;
    /**
     * 类型。视频 = 0, 音频 = 1。
     */
    @JsonProperty("codec_type")
    private int codecType;
    /**
     * 视频的帧率。
     */
    @JsonProperty("fps")
    private int fps;
    /**
     * 视频的高度。
     */
    @JsonProperty("height")
    private int height;
    /**
     * 轨道是否准备就绪。
     */
    @JsonProperty("ready")
    private boolean ready;
    /**
     * 视频的宽度。
     */
    @JsonProperty("width")
    private int width;

    /**
     * 损失
     */
    private String loss;
    // Getters and setters...
}