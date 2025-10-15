package com.shdatalink.sip.server.module.device.vo;

import com.shdatalink.sip.server.gb28181.core.bean.constants.DeviceTypeEnum;
import com.shdatalink.sip.server.gb28181.core.bean.constants.MediaStreamModeEnum;
import com.shdatalink.sip.server.gb28181.core.bean.constants.TransportTypeEnum;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import com.shdatalink.sip.server.module.device.enums.SIPProtocolEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DevicePage {
    /**
     * 设备配置ID
     */
    private Integer id;
    /**
     * 协议类型
     */
    private ProtocolTypeEnum protocolType;

    /**
     * 协议类型描述
     */
    private String protocolTypeAbbr;

    public String getProtocolTypeAbbr() {
        return protocolType.getAbbr();
    }
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
     * 传输协议
     */
    private TransportTypeEnum transport;
    /**
     * 接入协议类型
     */
    private SIPProtocolEnum sipProtocol;
    /**
     *  生产厂商
     */
    private String manufacturer;

    /**
     * 设备类型
     */
    private DeviceTypeEnum deviceType;

    /**
     * id是否已经关联设备
     */
    private Boolean enable;
    /**
     * 设备在线状态
     */
    private Boolean online = false;

    /**
     * 设备id关联设备时间
     */
    private LocalDateTime registerTime;
    /**
     * 出口ip
     */
    private String ipaddr;
    /**
     * 型号
     */
    private String model;
    /**
     * 版本
     */
    private String firmware;
    /**
     * 流传输模式
     */
    private MediaStreamModeEnum streamMode;
    /**
     * rtmp推流地址
     */
    private String streamUrl;
    /**
     * 流传输模式-文本
     */
    private String streamModeText;
    public String getStreamModeText() {
        if (streamMode == null)  return null;
        return streamMode.getText();
    }
    /**
     * 是否开启音频
     */
    private Boolean enableAudio;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    /**
     * 更新时间
     */
    private LocalDateTime lastModifiedTime;

    public static DevicePage fromDevice(Device device) {
        DevicePage devicePage = new DevicePage();
        devicePage.setDeviceId(device.getDeviceId());
        devicePage.setChannelCount(device.getChannelCount());
        devicePage.setName(device.getName());
        devicePage.setTransport(TransportTypeEnum.valueOf(device.getTransport()));
        devicePage.setSipProtocol(SIPProtocolEnum.GBT28181);
        devicePage.setManufacturer(devicePage.getManufacturer());
        devicePage.setEnable(device.getEnable());
        devicePage.setRegisterTime(device.getRegisterTime());
        devicePage.setIpaddr(device.getIp());
        devicePage.setModel(device.getModel());
        devicePage.setFirmware(device.getFirmware());
        devicePage.setStreamMode(device.getStreamMode());
        devicePage.setOnline(true);
        return devicePage;
    }
}
