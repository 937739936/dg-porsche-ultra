package com.shdatalink.sip.server.app.device.vo;

import com.shdatalink.sip.server.common.dto.PageParamWithGet;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.QueryParam;
import lombok.Data;

@Data
public class AppDeviceChannelPageParam extends PageParamWithGet {
    /**
     * 设备id
     */
    @NotBlank
    @QueryParam("deviceId")
    private String deviceId;
}