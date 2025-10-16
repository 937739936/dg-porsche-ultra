package com.shdatalink.sip.server.module.plan.vo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class VideoRecordDeviceParam {
    /**
     * 计划id
     */
    @NotNull(message = "录像计划id不能为空")
    private Integer planId;

    @Valid
    private List<Device> deviceList;

    @Data
    public static class Device {
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

}
