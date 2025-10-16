package com.shdatalink.sip.server.media.hook.req;

import lombok.Data;

@Data
public class ShellLoginReq {

    /**
     * TCP 连接唯一 ID，用于标识一次连接会话
     */
    private String id;

    /**
     * Telnet 终端 IP 地址
     */
    private String ip;

    /**
     * Telnet 终端登录用户密码
     */
    private String passwd;

    /**
     * Telnet 终端端口号（使用 int 类型兼容无符号短整型）
     */
    private Integer port;

    /**
     * Telnet 终端登录用户名
     */
    private String user_name;

    /**
     * 服务器 ID，通过配置文件设置，用于区分不同服务器
     */
    private String mediaServerId;
}