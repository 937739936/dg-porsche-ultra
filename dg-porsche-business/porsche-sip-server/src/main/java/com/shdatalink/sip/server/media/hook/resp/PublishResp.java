package com.shdatalink.sip.server.media.hook.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PublishResp extends HookResp {

    /**
     *  code 错误代码，0代表允许推流，非0代表拒绝
     */

    /**
     * msg 不允许推流时的错误提示信息
     */

    /**
     * 是否转换成 HLS-MPEGTS 协议
     */
    @JsonProperty("enable_hls")
    private Boolean enableHls;

    /**
     * 是否转换成 HLS-FMP4 协议
     */
    @JsonProperty("enable_hls_fmp4")
    private Boolean enableHlsFmp4;

    /**
     * 是否允许 MP4 录制
     */
    @JsonProperty("enable_mp4")
    private Boolean enableMp4;

    /**
     * 是否转 RTSP 协议
     */
    @JsonProperty("enable_rtsp")
    private Boolean enableRtsp;

    /**
     * 是否转 RTMP/FLV 协议
     */
    @JsonProperty("enable_rtmp")
    private Boolean enableRtmp;

    /**
     * 是否转 HTTP-TS/WS-TS 协议
     */
    @JsonProperty("enable_ts")
    private Boolean enableTs;

    /**
     * 是否转 HTTP-FMP4/WS-FMP4 协议
     */
    @JsonProperty("enable_fmp4")
    private Boolean enableFmp4;

    /**
     * HLS 协议是否有人观看才生成
     */
    @JsonProperty("hls_demand")
    private Boolean hlsDemand;

    /**
     * RTSP 协议是否有人观看才生成
     */
    @JsonProperty("rtsp_demand")
    private Boolean rtspDemand;

    /**
     * RTMP 协议是否有人观看才生成
     */
    @JsonProperty("rtmp_demand")
    private Boolean rtmpDemand;

    /**
     * TS 协议是否有人观看才生成
     */
    @JsonProperty("ts_demand")
    private Boolean tsDemand;

    /**
     * FMP4 协议是否有人观看才生成
     */
    @JsonProperty("fmp4_demand")
    private Boolean fmp4Demand;

    /**
     * 转协议时是否开启音频
     */
    @JsonProperty("enable_audio")
    private Boolean enableAudio;

    /**
     * 转协议时，无音频是否添加静音 AAC 音频
     */
    @JsonProperty("add_mute_audio")
    private Boolean addMuteAudio;

    /**
     * MP4 录制文件保存根目录，置空使用默认路径
     */
    @JsonProperty("mp4_save_path")
    private String mp4SavePath;

    /**
     * MP4 录制切片大小，单位秒
     */
    @JsonProperty("mp4_max_second")
    private Integer mp4MaxSecond;

    /**
     * MP4 录制是否当作观看者参与播放人数计数
     */
    @JsonProperty("mp4_as_player")
    private Boolean mp4AsPlayer;

    /**
     * HLS 文件保存根目录，置空使用默认路径
     */
    @JsonProperty("hls_save_path")
    private String hlsSavePath;

    /**
     * 该流是否开启时间戳覆盖
     * 0: 绝对时间戳
     * 1: 系统时间戳
     * 2: 相对时间戳
     */
    @JsonProperty("modify_stamp")
    private Integer modifyStamp;

    /**
     * 断连续推延时，单位毫秒，置空使用配置文件默认值
     */
    @JsonProperty("continue_push_ms")
    private Long continuePushMs;

    /**
     * 无人观看是否自动关闭流(不触发无人观看hook)
     */
    @JsonProperty("auto_close")
    private Boolean autoClose;

    /**
     * 是否修改流id，通过此参数可以自定义流id(譬如替换ssrc)
     */
    @JsonProperty("stream_replace")
    private String streamReplace;
}
