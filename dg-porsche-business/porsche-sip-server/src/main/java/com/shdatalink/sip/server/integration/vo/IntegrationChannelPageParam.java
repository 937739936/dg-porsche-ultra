package com.shdatalink.sip.server.integration.vo;

import com.shdatalink.sip.server.common.dto.PageParamWithGet;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class IntegrationChannelPageParam extends PageParamWithGet {
    /**
     * 设备id
     */
    @NotEmpty(message = "设备id必填")
    @QueryParam("deviceId")
    private List<String> deviceId;
}
