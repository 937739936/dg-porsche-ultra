package com.shdatalink.sip.service.module.config;


import com.shdatalink.sip.service.module.config.enums.ConfigTypesEnum;
import com.shdatalink.sip.service.module.config.service.ConfigService;
import com.shdatalink.sip.service.module.config.vo.ConfigSetParam;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

/**
 * 配置
 */
@Path("admin/config")
public class ConfigController {

    @Inject
    ConfigService configService;

    /**
     * 获取配置
     */
    @GET
    @Path("get")
    public Object get(@QueryParam("type") ConfigTypesEnum type) {
        return configService.getConfig(type);
    }

    /**
     * 保存配置
     */
    @POST
    @Path("set")
    public boolean set(@Valid ConfigSetParam param) {
        configService.setConfig(param.getType(), param.getValue());
        return true;
    }
}
