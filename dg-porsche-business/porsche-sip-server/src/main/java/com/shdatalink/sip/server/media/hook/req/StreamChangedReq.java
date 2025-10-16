package com.shdatalink.sip.server.media.hook.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class StreamChangedReq {

    /**
     * 流应用名，标识特定的应用或业务场景
     */
    private String app;

    /**
     * 流操作类型标识
     * true: 流注册操作
     * false: 流注销操作
     */
    private Boolean regist;

    /**
     * 流协议类型，固定为 "rtsp" 或 "rtmp"
     */
    private String schema;

    /**
     * 流 ID，唯一标识一个媒体流
     */
    private String stream;

    /**
     * 流虚拟主机，用于逻辑隔离不同租户或域名
     */
    private String vhost;

    /**
     * 服务器 ID，通过配置文件设置，用于区分不同服务器
     */
    private String mediaServerId;


    @JsonProperty("aliveSecond")
    private Integer aliveSecond;
    @JsonProperty("bytesSpeed")
    private Integer bytesSpeed;
    @JsonProperty("createStamp")
    private Integer createStamp;
    @JsonProperty("hook_index")
    private Integer hookIndex;
    @JsonProperty("isRecordingHLS")
    private Boolean isRecordingHLS;
    @JsonProperty("isRecordingMP4")
    private Boolean isRecordingMP4;
    @JsonProperty("originSock")
    private OriginSockDTO originSock;
    @JsonProperty("originType")
    private Integer originType;
    @JsonProperty("originTypeStr")
    private String originTypeStr;
    @JsonProperty("originUrl")
    private String originUrl;
    @JsonProperty("params")
    private String params;
    @JsonProperty("readerCount")
    private Integer readerCount;
    @JsonProperty("totalBytes")
    private Integer totalBytes;
    @JsonProperty("totalReaderCount")
    private Integer totalReaderCount;
    @JsonProperty("tracks")
    private List<TracksDTO> tracks;

    @NoArgsConstructor
    @Data
    public static class OriginSockDTO {
        @JsonProperty("identifier")
        private String identifier;
        @JsonProperty("local_ip")
        private String localIp;
        @JsonProperty("local_port")
        private Integer localPort;
        @JsonProperty("peer_ip")
        private String peerIp;
        @JsonProperty("peer_port")
        private Integer peerPort;
    }

    @NoArgsConstructor
    @Data
    public static class TracksDTO {
        @JsonProperty("channels")
        private Integer channels;
        @JsonProperty("codec_id")
        private Integer codecId;
        @JsonProperty("codec_id_name")
        private String codecIdName;
        @JsonProperty("codec_type")
        private Integer codecType;
        @JsonProperty("duration")
        private Integer duration;
        @JsonProperty("frames")
        private Integer frames;
        @JsonProperty("loss")
        private Integer loss;
        @JsonProperty("ready")
        private Boolean ready;
        @JsonProperty("sample_bit")
        private Integer sampleBit;
        @JsonProperty("sample_rate")
        private Integer sampleRate;
        @JsonProperty("fps")
        private Integer fps;
        @JsonProperty("gop_interval_ms")
        private Integer gopIntervalMs;
        @JsonProperty("gop_size")
        private Integer gopSize;
        @JsonProperty("height")
        private Integer height;
        @JsonProperty("key_frames")
        private Integer keyFrames;
        @JsonProperty("width")
        private Integer width;
    }
}
