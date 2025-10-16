package com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.enums.CmdType;
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
public abstract class QueryMessage {

    protected CmdType cmdType;

    @JacksonXmlProperty(localName = "SN")
    private String sn;

    /**
     * 目标设备的设备编码(必选)
     */
    @JacksonXmlProperty(localName = "DeviceID")
    private String deviceId;
}
