package com.shdatalink.sip.server.media.hook.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RecordMp4Req {

    /**
     * 录制的流应用名，标识特定的应用或业务场景
     */
    private String app;

    /**
     * 录制生成的文件名（包含扩展名）
     */
    @JsonProperty("file_name")
    private String fileName;

    /**
     * 录制文件的绝对路径
     */
    @JsonProperty("file_path")
    private String filePath;

    /**
     * 录制文件大小，单位为字节
     */
    @JsonProperty("file_size")
    private Integer fileSize;

    /**
     * 录制文件所在的目录路径
     */
    private String folder;

    /**
     * 开始录制的时间戳（毫秒级）
     */
    @JsonProperty("start_time")
    private Long startTime;

    /**
     * 录制的流ID，唯一标识被录制的媒体流
     */
    private String stream;

    /**
     * 录制时长，单位为秒（精确到小数点后两位）
     */
    @JsonProperty("time_len")
    private Float timeLen;

    /**
     * 录制文件的HTTP/RTSP/RTMP点播相对URL路径
     */
    private String url;

    /**
     * 流虚拟主机，用于逻辑隔离不同租户或域名
     */
    private String vhost;

    /**
     * 服务器ID，通过配置文件设置，用于区分不同服务器
     */
    private String mediaServerId;
}