package com.shdatalink.sip.server.media.bean.entity.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * This class represents a stream with various properties.
 */
@Data
public class AddFFmpegSourceReq {

    /**
     * FFmpeg拉流地址,支持任意协议或格式(只要FFmpeg支持即可)
     */
    @JsonProperty("src_url")
    private String  srcUrl;

    /**
     * FFmpeg rtmp推流地址，一般都是推给自己，例如rtmp://127.0.0.1/live/stream_form_ffmpeg
     */
    @JsonProperty("dst_url")
    private String  dstUrl;

    /**
     * FFmpeg推流成功超时时间
     */
    @JsonProperty("timeout_ms")
    private Integer timeoutMs;

    /**
     * 是否开启hls录制
     */
    @JsonProperty("enable_hls")
    private Boolean enableHls;

    /**
     * 是否开启mp4录制
     */
    @JsonProperty("enable_mp4")
    private Boolean enableMp4;

    /**
     * 配置文件中FFmpeg命令参数模板key(非内容)，置空则采用默认模板:ffmpeg.cmd
     */
    @JsonProperty("ffmpeg_cmd_key")
    private String  ffmpegCmdKey;

}
