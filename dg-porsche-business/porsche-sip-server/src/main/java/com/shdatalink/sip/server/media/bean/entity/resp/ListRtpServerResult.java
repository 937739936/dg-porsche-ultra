package com.shdatalink.sip.server.media.bean.entity.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ListRtpServerResult {

    private String port;

    @JsonProperty("stream_id")
    private String streamId;
}
