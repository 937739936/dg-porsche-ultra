package com.shdatalink.sip.server.media.hook.resp;

import lombok.Data;

@Data
public class HttpAccessResp extends HookResp {

    /**
     * 错误提示信息
     * 若允许访问，此字段为空字符串
     * 若禁止访问，此字段包含具体的错误描述
     */
    private String err;

    /**
     * 客户端能访问或被禁止的顶端目录
     * 空字符串表示当前目录
     */
    private String path;

    /**
     * 本次授权结果的有效期，单位为秒
     */
    private Integer second;

    /**
     * 服务器ID，通过配置文件设置，用于区分不同服务器
     */
    private String mediaServerId;
}