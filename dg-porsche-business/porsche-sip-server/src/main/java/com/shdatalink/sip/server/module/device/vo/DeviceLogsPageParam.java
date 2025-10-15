package com.shdatalink.sip.server.module.device.vo;

import com.shdatalink.sip.server.common.dto.PageParamWithGet;
import jakarta.ws.rs.QueryParam;
import lombok.Data;

@Data
public class DeviceLogsPageParam extends PageParamWithGet {
    /**
     * 设备id（国标）
     */
    @QueryParam("deviceId")
    private String deviceId;
    /**
     * 通道id
     */
    @QueryParam("channelId")
    private String channelId;
}
