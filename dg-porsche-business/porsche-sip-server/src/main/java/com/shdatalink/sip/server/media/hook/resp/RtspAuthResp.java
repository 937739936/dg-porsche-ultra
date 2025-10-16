package com.shdatalink.sip.server.media.hook.resp;

import lombok.Data;

@Data
public class RtspAuthResp {

    /**
     * 错误代码，0代表允许播放，非0代表拒绝
     */
    private Integer code;

    /**
     * 播放鉴权失败时的错误提示信息
     */
    private String msg;

    /**
     * 用户密码是否为加密摘要
     * true: 密码字段是MD5摘要（格式：md5(username:realm:password)）
     * false: 密码字段是明文
     */
    private Boolean encrypted;

    /**
     * 用户密码（明文或MD5摘要）
     * 当encrypted为true时，格式为md5(username:realm:password)
     * 当encrypted为false时，为原始明文密码
     */
    private String passwd;
}