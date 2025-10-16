package com.shdatalink.sip.server.media.hook.req;

import lombok.Data;

import java.util.Map;

@Data
public class HttpAccessReq {

    /**
     * HTTP客户端请求头，键值对形式存储所有请求头信息
     */
    private Map<String, String> header;

    /**
     * TCP连接唯一ID，用于标识一次连接会话
     */
    private String id;

    /**
     * HTTP客户端IP地址
     */
    private String ip;

    /**
     * HTTP访问路径类型标识：true表示目录，false表示文件
     */
    private Boolean is_dir;

    /**
     * HTTP URL的参数部分，包含查询字符串等信息
     */
    private String params;

    /**
     * 请求访问的文件或目录路径
     */
    private String path;

    /**
     * HTTP客户端端口号（使用int类型兼容无符号短整型）
     */
    private Integer port;

    /**
     * 服务器ID，通过配置文件设置，用于区分不同服务器
     */
    private String mediaServerId;
}