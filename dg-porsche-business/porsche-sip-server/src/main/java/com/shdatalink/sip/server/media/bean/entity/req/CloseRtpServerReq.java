package com.shdatalink.sip.server.media.bean.entity.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CloseRtpServerReq {

    private String port;

    @JsonProperty("stream_id")
    private String streamId;
}
