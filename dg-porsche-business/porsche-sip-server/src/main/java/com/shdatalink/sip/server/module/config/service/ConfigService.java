package com.shdatalink.sip.server.module.config.service;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shdatalink.framework.json.utils.JsonUtil;
import com.shdatalink.sip.server.module.config.entity.Config;
import com.shdatalink.sip.server.module.config.enums.ConfigTypesEnum;
import com.shdatalink.sip.server.module.config.mapper.ConfigMapper;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
public class ConfigService extends ServiceImpl<ConfigMapper, Config> {

    public <T> T getConfig(ConfigTypesEnum type) {
        ObjectNode objectNode = JsonUtil.getObjectMapper().createObjectNode();
        for (Config config : baseMapper.selectByType(type)) {
            objectNode.put(config.getName(), config.getValue());
        }
        return JsonUtil.parseObject(JsonUtil.toJsonString(objectNode), type.getClazz());
    }

    @Transactional(rollbackOn = Exception.class)
    public void setConfig(ConfigTypesEnum type, ObjectNode config) {
        baseMapper.deleteByType(type);
        List<Config> list = config.properties().stream().map(obj -> {
            Config c = new Config();
            c.setName(obj.getKey());
            c.setValue(obj.getValue() == null ? null : obj.getValue().asText());
            c.setType(type);
            return c;
        }).toList();
        saveBatch(list);
    }


}
