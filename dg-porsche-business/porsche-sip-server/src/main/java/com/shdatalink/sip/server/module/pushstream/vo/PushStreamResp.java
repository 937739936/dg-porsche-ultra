package com.shdatalink.sip.server.module.pushstream.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PushStreamResp {
    /**
     * ID
     */
    private String id;
    /**
     * 协议
     */
    private String protocol;
    /**
     * 远程地址
     */
    private String remoteAddress;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 最后更新时间
     */
    private LocalDateTime lastModifiedTime;


}
