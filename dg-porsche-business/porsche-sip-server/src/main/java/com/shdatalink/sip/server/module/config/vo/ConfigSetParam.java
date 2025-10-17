package com.shdatalink.sip.server.module.config.vo;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shdatalink.sip.server.module.config.enums.ConfigTypesEnum;
import jakarta.ws.rs.QueryParam;
import lombok.Data;

@Data
public class ConfigSetParam {
    /**
     * 配置类型
     */
    @QueryParam("type")
    private ConfigTypesEnum type;
    /**
     * 配置值
     */
    @QueryParam("value")
    private ObjectNode value;


}
