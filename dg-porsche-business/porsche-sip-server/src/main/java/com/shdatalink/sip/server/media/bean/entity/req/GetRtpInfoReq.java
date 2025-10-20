package com.shdatalink.sip.server.media.bean.entity.req;

import jakarta.ws.rs.QueryParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetRtpInfoReq {
    @QueryParam("stream_id")
    private String streamId;
}
