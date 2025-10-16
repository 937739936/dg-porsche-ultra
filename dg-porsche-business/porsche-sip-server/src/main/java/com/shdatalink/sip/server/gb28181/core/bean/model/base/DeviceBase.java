package com.shdatalink.sip.server.gb28181.core.bean.model.base;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 设备响应基础数据
 *
 */
@Getter
@Setter
public class DeviceBase {
    @JacksonXmlProperty(localName = "CmdType")
    private String cmdType;

    @JacksonXmlProperty(localName = "SN")
    private String sn;

    @JacksonXmlProperty(localName = "DeviceID")
    private String deviceId;

    public DeviceBase() {
    }

    public DeviceBase(String cmdType, String sn, String deviceId) {
        this.cmdType = cmdType;
        this.sn = sn;
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "DeviceBase{" +
                "cmdType='" + cmdType + '\'' +
                ", sn='" + sn + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }
}
