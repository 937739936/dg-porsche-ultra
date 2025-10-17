package com.shdatalink.sip.server.integration.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IntegrationPtzControlStopParam {
    /**
     * 通道id
     */
    @NotBlank(message = "通道id不能为空")
    private String channelId;

    /**
     * 操作序列
     */
    @NotBlank(message = "serialNo不能为空")
    private String serialNo;
}
