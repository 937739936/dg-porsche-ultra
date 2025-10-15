package com.shdatalink.sip.server.module.device.vo;

import com.shdatalink.sip.server.module.device.enums.PtzTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DevicePtzTypeModifyParam {
    /**
     * 云台类型
     */
    @NotNull(message = "云台类型不能为空")
    private PtzTypeEnum ptzType;
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
}
