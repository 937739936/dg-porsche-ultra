package com.shdatalink.sip.server.module.config.vo;

import lombok.Data;

/**
 * 系统PC页面样式配置
 */
@Data
public class UserStyleConfigVO {

    private Integer id;
    /**
     * 主题色
     */
    private String colorPrimary;
    /**
     * 侧边背景色
     */
    private String siderBg;
    /**
     * 侧边文字色
     */
    private String siderColor;
    /**
     * 头部背景色
     */
    private String headerBg;
    /**
     * 头部文字色
     */
    private String headerColor;
    /**
     * 底部背景色
     */
    private String footerBg;
    /**
     * 底部文字色
     */
    private String footerColor;
    /**
     * 主体背景色
     */
    private String bodyBg;
    /**
     * 主体区域背景色
     */
    private String bodyBlockBg;
    /**
     * 主体区域文字色
     */
    private String bodyBlockColor;
    /**
     * logo颜色
     */
    private String logoColor;

}
