package com.shdatalink.sip.server.media.bean.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 这个类代表了具有各种属性的会话。
 */
@Data
public class MediaInfo {

    /**
     * 状态码。
     */
    @JsonProperty("code")
    private int code;

    /**
     * 本协议的观看人数。
     */
    @JsonProperty("readerCount")
    private int readerCount;

    /**
     * 观看总人数，包括hls/rtsp/rtmp/http-flv/ws-flv。
     */
    @JsonProperty("totalReaderCount")
    private int totalReaderCount;

    /**
     * 轨道列表。
     */
    @JsonProperty("tracks")
    private List<Track> tracks;

    /**
     * 流
     */
    private String stream;

    /**
     * 正在录制mp4
     */
    private Boolean isRecordingMP4;

    /**
     * 正在记录HLS
     */
    private Boolean isRecordingHLS;


    /**
     * 这个类代表了具有各种属性的轨道。
     */

}
