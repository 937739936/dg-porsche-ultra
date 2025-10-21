package com.shdatalink.sip.server.module.config.vo;

import lombok.Data;


/**
 * 系统基础配置
 */
@Data
public class SystemBaseConfig {
    /**
     * 平台名称
     */
    private String name;
    /**
     * 小logo图片地址
     */
    private String logoSmall;
    /**
     * 大logo图片地址
     */
    private String logoLarge;
    /**
     * 备案号
     */
    private String recordNumber;
    /**
     * 登录页背景图片地址
     */
    private String loginImg;
}
