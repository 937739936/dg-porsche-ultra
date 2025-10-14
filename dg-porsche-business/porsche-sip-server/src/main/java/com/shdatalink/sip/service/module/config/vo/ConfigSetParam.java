package com.shdatalink.sip.service.module.config.vo;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shdatalink.sip.service.module.config.enums.ConfigTypesEnum;
import lombok.Data;

@Data
public class ConfigSetParam {
    /**
     * 配置类型
     */
    private ConfigTypesEnum type;
    /**
     * 配置值
     */
    private ObjectNode value;


}
