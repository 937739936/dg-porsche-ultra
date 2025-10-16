package com.shdatalink.sip.server.media.bean.entity.resp;

import lombok.Data;


@Data
public class OpenRtpServerResult extends MediaServerResponse<Void> {

    /**
     * 端口
     */
    private int port;
}
