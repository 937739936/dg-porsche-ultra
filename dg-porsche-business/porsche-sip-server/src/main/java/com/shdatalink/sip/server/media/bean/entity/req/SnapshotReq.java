package com.shdatalink.sip.server.media.bean.entity.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 这个类代表了具有各种属性的截图任务。
 */
@Data
public class SnapshotReq {

    /**
     * 需要截图的url，可以是本机的，也可以是远程主机的。
     */
    @JsonProperty("url")
    private String url;

    /**
     * 截图失败超时时间，防止FFmpeg一直等待截图。
     */
    @JsonProperty("timeout_sec")
    private int timeoutSec;

    /**
     * 截图的过期时间，该时间内产生的截图都会作为缓存返回。
     */
    @JsonProperty("expire_sec")
    private int expireSec;
}
