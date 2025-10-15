package com.shdatalink.sip.server.module.device.vo;

import com.shdatalink.sip.server.common.dto.PageParamWithGet;
import com.shdatalink.sip.server.gb28181.core.bean.constants.DeviceManufacturerEnum;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import jakarta.ws.rs.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DevicePageParam extends PageParamWithGet {
    /**
     * 协议类型
     */
    @QueryParam("protocolType")
    private ProtocolTypeEnum protocolType;

    /**
     * 在线状态 true-在线 false-离线 null-不筛选
     */
    @QueryParam("online")
    private Boolean online;

    /**
     * 设备名称或编码
     */
    @QueryParam("keyword")
    private String keyword;
    /**
     * 厂商
     */
    @QueryParam("manufacturer")
    private DeviceManufacturerEnum manufacturer;
}
