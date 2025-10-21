package com.shdatalink.sip.server.module.config.service;


import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shdatalink.framework.json.utils.JsonUtil;
import com.shdatalink.sip.server.common.constants.CommonConstants;
import com.shdatalink.sip.server.module.config.entity.Config;
import com.shdatalink.sip.server.module.config.enums.ConfigTypesEnum;
import com.shdatalink.sip.server.module.config.mapper.ConfigMapper;
import com.shdatalink.sip.server.module.config.vo.SystemBaseConfig;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.config.service.ConfigService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@Slf4j
@ApplicationScoped
public class ConfigService extends ServiceImpl<ConfigMapper, Config> {

    @ConfigProperty(name = "attach.upload.path")
    String uploadPath;


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


    public void initSystemBase() {
        //获取默认配置
        SystemBaseConfig systemBaseConfig = getConfig(ConfigTypesEnum.SystemBase);
        if(systemBaseConfig == null){
            throw new RuntimeException("初始化默认样式配置失败");
        }
        String smallLogoPath = uploadPath + File.separator + systemBaseConfig.getLogoSmall();
        boolean b = copyFile(smallLogoPath, CommonConstants.DEFAULT_SMALL_LOGO_PATH);
        if(!b){
            log.error("复制logo失败");
        }
        String largeLogoPath = uploadPath + File.separator + systemBaseConfig.getLogoLarge();
        boolean b1 = copyFile(largeLogoPath, CommonConstants.DEFAULT_LARGE_LOGO_PATH);
        if(!b1){
            log.error("复制logo失败");
        }
    }

    public boolean copyFile(String filePath, String sourcePath){
        File targetFile = new File(filePath);
        if(!targetFile.getParentFile().exists()){
            if(!targetFile.getParentFile().mkdirs()){
                log.error("创建目录失败：{}", targetFile.getParentFile().getPath());
                return false;
            }
        }
        if(!targetFile.exists()){
            try {
                InputStream in = getClass().getClassLoader().getResourceAsStream(sourcePath);
                if (in == null) {
                    throw new IllegalArgumentException("Resource not found: " + sourcePath);
                }
                Path targetPath = Paths.get(targetFile.getAbsolutePath());
                Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("✅ 默认 logo 已复制到：" + targetFile.getPath());
            } catch (Exception e) {
                log.error("复制默认logo文件失败", e);
            }
        }
        return true;
    }
}
