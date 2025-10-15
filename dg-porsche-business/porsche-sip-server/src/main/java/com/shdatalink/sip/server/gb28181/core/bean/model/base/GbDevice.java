package com.shdatalink.sip.server.gb28181.core.bean.model.base;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.shdatalink.sip.server.gb28181.core.bean.constants.MediaStreamModeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@AllArgsConstructor
@Data
@Builder
public class GbDevice {

    /**
     * 设备id
     */
    private String channelId;

    /**
     * IP
     */
    private String ip;
    /**
     * 端口
     */
    private int port;

    /**
     * 传输方式,默认UDP
     */
    private String transport;

    /**
     * 流传输模式
     */
    private MediaStreamModeEnum streamMode;

}
