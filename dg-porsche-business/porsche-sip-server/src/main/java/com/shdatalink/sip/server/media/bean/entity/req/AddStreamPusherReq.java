package com.shdatalink.sip.server.media.bean.entity.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class represents a stream with various properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AddStreamPusherReq extends MediaReq {

    /**
     * 目标转推url，带参数需要自行url转义
     */
    @JsonProperty("dst_url")
    private String dstUrl;

    /**
     * 转推失败重试次数，默认无限重试
     */
    @JsonProperty("retry_count")
    private Integer retryCount;

    /**
     * rtsp推流时，推流方式，0：tcp，1：udp
     */
    @JsonProperty("rtp_type")
    private Integer rtpType;

    /**
     * 推流超时时间，单位秒，float类型
     */
    @JsonProperty("timeout_sec")
    private Integer timeoutSec;

    /**
     * srt推流的密码
     */
    private String passphrase;

}
