package com.shdatalink.sip.server.media.bean.entity.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MediaVersion {

    @JsonProperty("buildTime")
    private String buildTime;

    @JsonProperty("branchName")
    private String branchName;

    @JsonProperty("commitHash")
    private String commitHash;
}