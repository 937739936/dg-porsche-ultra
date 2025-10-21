package com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.enums.CmdType;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 设备查询基础数据
 *
 */
@JacksonXmlRootElement(localName = "Query")
@JsonRootName("Query")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@RegisterForReflection
public abstract class SubscribeMessage {

    protected CmdType cmdType;

    @JacksonXmlProperty(localName = "SN")
    private String sn;

    /**
     * 目标设备的设备编码(必选)
     */
    @JacksonXmlProperty(localName = "DeviceID")
    private String deviceId;

    /**
     * 是否订阅，true:订阅；false: 取消订阅；
     */
    @JsonIgnore
    private Boolean isSubscribe;
}
