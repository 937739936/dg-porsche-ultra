package com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.response;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.DeviceBase;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = true)
@JacksonXmlRootElement(localName = "Response")
@ToString
@RegisterForReflection
public class DeviceInfo extends DeviceBase {

    /**
     * 结果
     */
    private String Result;

    /**
     * 制造商
     */
    private String Manufacturer;

    /**
     * 模型
     */
    private String Model;

    /**
     * 固件
     */
    private String Firmware;

}
