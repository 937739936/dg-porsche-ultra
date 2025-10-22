package com.shdatalink.sip.server.integration.vo;

import com.shdatalink.sip.server.common.dto.PageParamWithGet;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class IntegrationDeviceChannelPageParam extends PageParamWithGet {
    /**
     * 设备id
     */
    @NotBlank
    private String deviceId;
}
