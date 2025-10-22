package com.shdatalink.sip.server.media.bean.entity.resp;

import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class OpenRtpServerResult extends MediaServerResponse<Void> {

    /**
     * 端口
     */
    private int port;
}
