package com.shdatalink.sip.server.media.bean.entity.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StopSendRtpReq extends MediaReq {

    /**
     * 停止GB28181 ps-rtp推流
     */
    private String ssrc;

}
