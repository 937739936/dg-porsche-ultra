package com.shdatalink.sip.server.integration.vo;

import com.shdatalink.sip.server.common.dto.PageParamWithGet;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class IntegrationChannelPageParam extends PageParamWithGet {
    /**
     * 设备id
     */
    @NotEmpty(message = "设备id必填")
    private List<String> deviceId;
}
