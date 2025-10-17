package com.shdatalink.sip.server.integration.vo;

import com.shdatalink.sip.server.gb28181.core.bean.constants.DeviceTypeEnum;
import lombok.Data;

@Data
public class IntegrationDeviceDetail {
    /**
     * 设备名称
     */
    private String name;
    /**
     * 设备id
     */
    private String deviceId;
    /**
     * 通道数
     */
    private Integer channelCount;
    /**
     *  生产厂商
     */
    private String manufacturer;

    /**
     * 设备在线状态
     */
    private Boolean online = false;
    /**
     * 出口ip
     */
    private String ipaddr;
    /**
     * 设备类型
     */
    private DeviceTypeEnum deviceType;
}
