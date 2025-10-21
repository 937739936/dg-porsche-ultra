package com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.DeviceBase;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

/**
 * 设备目录响应数据
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JacksonXmlRootElement(localName = "Notify")
@RegisterForReflection
public class DeviceAlarm extends DeviceBase {

    @JacksonXmlProperty(localName = "AlarmPriority")
    public String alarmPriority;

    @JacksonXmlProperty(localName = "AlarmMethod")
    public String alarmMethod;

    /**
     * ISO8601
     */
    @JacksonXmlProperty(localName = "AlarmTime")
    public String alarmTime;

    /**
     * 经度
     */
    @JacksonXmlProperty(localName = "Longitude")
    public String longitude;

    /**
     * 纬度
     */
    @JacksonXmlProperty(localName = "Latitude")
    public String latitude;

    @JacksonXmlProperty(localName = "AlarmType")
    public String alarmType;

    @JacksonXmlProperty(localName = "Info")
    private AlarmInfo info;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @JacksonXmlRootElement(localName = "Info")
    public static class AlarmInfo {

        @JacksonXmlProperty(localName = "AlarmType")
        public String alarmType;

        @JacksonXmlProperty(localName = "AlarmTypeParam")
        public AlarmTypeParam alarmTypeParam;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @JacksonXmlRootElement(localName = "AlarmTypeParam")
        public static class AlarmTypeParam{

            @JacksonXmlProperty(localName = "EventType")
            private String eventType;
        }
    }


}
