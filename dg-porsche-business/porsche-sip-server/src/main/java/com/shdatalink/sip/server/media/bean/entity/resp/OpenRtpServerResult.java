package com.shdatalink.sip.server.media.bean.entity.resp;

import lombok.Data;


@Data
public class OpenRtpServerResult {

    /**
     * 端口
     */
    private int port;

    /**
     * 状态码
     */
    private int code;

    private String msg;

}
