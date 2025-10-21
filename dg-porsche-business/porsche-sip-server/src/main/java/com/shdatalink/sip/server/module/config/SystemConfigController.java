package com.shdatalink.sip.server.module.config;


import com.shdatalink.framework.common.annotation.Anonymous;
import com.shdatalink.sip.server.module.config.enums.ConfigTypesEnum;
import com.shdatalink.sip.server.module.config.service.ConfigService;
import com.shdatalink.sip.server.module.config.vo.SystemBaseConfig;
import io.quarkus.runtime.Startup;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

/**
 * 配置
 */
@Path("admin/config")
public class SystemConfigController {

    @Inject
    ConfigService configService;

    @Startup
    public void init() {
        configService.initSystemBase();
    }

    /**
     * 系统基础配置
     */
    @GET
    @Path("systemBaseConfig")
    @Anonymous
    public SystemBaseConfig systemBaseConfig() {
        return configService.getConfig(ConfigTypesEnum.SystemBase);
    }


}
