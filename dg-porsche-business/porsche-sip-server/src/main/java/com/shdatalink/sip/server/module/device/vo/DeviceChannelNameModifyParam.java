package com.shdatalink.sip.server.module.device.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeviceChannelNameModifyParam {
    /**
     * 云台类型
     */
    @NotBlank(message = "通道名称不能为空")
    private String name;
    /**
     * 设备id
     */
    private String deviceId;
    /**
     * 通道id
     */
    @NotBlank(message = "通道id不能为空")
    private String channelId;
}
