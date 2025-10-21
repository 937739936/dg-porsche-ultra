package com.shdatalink.sip.server.app.device.vo;

import com.shdatalink.sip.server.gb28181.core.bean.constants.DeviceTypeEnum;
import lombok.Data;

@Data
public class AppDevicePage {
    /**
     * 设备配置ID
     */
    private Integer id;
    /**
     * 设备名称
     */
    private String name;
    /**
     * 设备id（国标id）
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
    private DeviceTypeEnum deviceType;
}
