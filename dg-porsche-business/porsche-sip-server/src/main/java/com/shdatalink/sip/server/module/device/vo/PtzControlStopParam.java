package com.shdatalink.sip.server.module.device.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PtzControlStopParam {
    /**
     * 设备id
     */
    @NotBlank(message = "设备id不能为空")
    private String deviceId;
    /**
     * 通道id
     */
    @NotBlank(message = "通道id不能为空")
    private String channelId;

    @NotBlank(message = "serialNo不能为空")
    private String serialNo;
}
