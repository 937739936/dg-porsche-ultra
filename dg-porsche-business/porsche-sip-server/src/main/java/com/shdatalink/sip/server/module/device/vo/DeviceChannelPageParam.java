package com.shdatalink.sip.server.module.device.vo;

import com.shdatalink.sip.server.common.dto.PageParamWithGet;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeviceChannelPageParam extends PageParamWithGet {
    /**
     * 设备id(国标)
     */
    @QueryParam("deviceId")
    @NotBlank(message = "设备id必须")
    private String deviceId;
    /**
     * 通道名称
     */
    @QueryParam("name")
    private String name;
    /**
     * 在线状态 true-在线 false-离线 null-不筛选
     */
    @QueryParam("online")
    private Boolean online;
}
