package com.shdatalink.sip.server.media.bean.entity.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StartSendRtpResult {

    private String code;

    @JsonProperty("local_port")
    private String localPort;
}
