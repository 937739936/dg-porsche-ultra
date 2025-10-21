package com.shdatalink.sip.server.module.config.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 系统PC页面样式配置
 */
@Data
public class UserStyleConfigSaveReq {

    /**
     * 主题色
     */
    @NotBlank(message = "主题色不能为空")
    @Length(max = 100, message = "主题色不能超过{max}个字符")
    private String colorPrimary;
    /**
     * 侧边背景色
     */
    @NotBlank(message = "侧边背景色不能为空")
    @Length(max = 100, message = "侧边背景色不能超过{max}个字符")
    private String siderBg;
    /**
     * 侧边文字色
     */
    @NotBlank(message = "侧边文字色不能为空")
    @Length(max = 100, message = "侧边文字色不能超过{max}个字符")
    private String siderColor;
    /**
     * 头部背景色
     */
    @NotBlank(message = "头部背景色不能为空")
    @Length(max = 100, message = "头部背景色不能超过{max}个字符")
    private String headerBg;
    /**
     * 头部文字色
     */
    @NotBlank(message = "头部文字色不能为空")
    @Length(max = 100, message = "头部文字色不能超过{max}个字符")
    private String headerColor;
    /**
     * 底部背景色
     */
    @NotBlank(message = "底部背景色不能为空")
    @Length(max = 100, message = "底部背景色不能超过{max}个字符")
    private String footerBg;
    /**
     * 底部文字色
     */
    @NotBlank(message = "底部文字色不能为空")
    @Length(max = 100, message = "底部文字色不能超过{max}个字符")
    private String footerColor;
    /**
     * 主体背景色
     */
    @NotBlank(message = "主体背景色不能为空")
    @Length(max = 100, message = "主体背景色不能超过{max}个字符")
    private String bodyBg;
    /**
     * 主体区域背景色
     */
    @NotBlank(message = "主体区域背景色不能为空")
    @Length(max = 100, message = "主体背景色不能超过{max}个字符")
    private String bodyBlockBg;
    /**
     * 主体区域文字色
     */
    @NotBlank(message = "主体区域文字色不能为空")
    @Length(max = 100, message = "主体背景色不能超过{max}个字符")
    private String bodyBlockColor;
    /**
     * logo颜色
     */
    @Length(max = 100, message = "logo颜色不能超过{max}个字符")
    private String logoColor;
}
