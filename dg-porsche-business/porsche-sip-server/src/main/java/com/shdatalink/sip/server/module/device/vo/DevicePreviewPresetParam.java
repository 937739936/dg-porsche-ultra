package com.shdatalink.sip.server.module.device.vo;

import com.shdatalink.sip.server.common.validation.annotation.ValueList;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
public class DevicePreviewPresetParam {
    /**
     * 名称
     */
    @NotBlank
    @Length(max = 10, message = "名称长度最多10字")
    private String name;

    /**
     * 屏幕数
     */
    @NotNull
    @ValueList(value = {"1","4","9","16"}, message = "屏幕数参数不合法，允许值{value}")
    private Integer screenCount;

    /**
     * 播放通道列表
     */
    @NotEmpty
    @Valid
    private List<Device> screenList;

    @Data
    public static class Device {
        /**
         * 设备id
         */
        @NotBlank
        private String deviceId;
        /**
         * 通道id
         */
        @NotBlank
        private String channelId;
    }
}
