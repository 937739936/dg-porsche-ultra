package com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.DeviceBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备目录响应数据
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JacksonXmlRootElement(localName = "Notify")
public class DeviceMobilePosition extends DeviceBase {

    /**
     * 产生通知时间（必选）
     */
    @JacksonXmlProperty(localName = "Time")
    private String time;

    /**
     * 经度（必选）- GB28181标准要求double类型
     */
    @JacksonXmlProperty(localName = "Longitude")
    private Double longitude;

    /**
     * 纬度（必选）- GB28181标准要求double类型
     */
    @JacksonXmlProperty(localName = "Latitude")
    private Double latitude;

    /**
     * 速度，单位：km/h（可选）- GB28181标准要求double类型
     */
    @JacksonXmlProperty(localName = "Speed")
    private Double speed;

    /**
     * 方向，取值为当前摄像头方向与正北方的顺时针夹角，取值范围0°～360°，单位：(°)（可选）
     * GB28181标准要求double类型
     */
    @JacksonXmlProperty(localName = "Direction")
    private Double direction;

    /**
     * 海拔高度，单位：m（可选）- GB28181标准要求double类型
     */
    @JacksonXmlProperty(localName = "Altitude")
    private Double altitude;


}
