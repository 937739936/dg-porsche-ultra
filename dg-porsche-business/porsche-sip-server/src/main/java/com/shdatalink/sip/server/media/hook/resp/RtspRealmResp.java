package com.shdatalink.sip.server.media.hook.resp;

import lombok.Data;

@Data
public class RtspRealmResp {

    /**
     * 状态码，固定返回0表示成功
     */
    private Integer code;

    /**
     * RTSP流鉴权 realm
     * 空字符串表示不需要鉴权
     * 非空字符串表示需要使用该realm进行RTSP专有鉴权
     */
    private String realm;
}