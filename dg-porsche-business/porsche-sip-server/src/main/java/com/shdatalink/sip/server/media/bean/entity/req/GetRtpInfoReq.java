package com.shdatalink.sip.server.media.bean.entity.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetRtpInfoReq {
    private String streamId;
}
