package com.shdatalink.sip.server.media.bean.entity.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class CloseStreamsResult {

    private int code;

    /**
     * 筛选命中的流个数
     */
    @JsonProperty("count_hit")
    private String countHit;


    /**
     * 被关闭的流个数，可能小于count_hit
     */
    @JsonProperty("count_closed")
    private String countClosed;

}
