package com.shdatalink.sip.server.module.config;

import com.shdatalink.sip.server.module.config.convert.UserStyleConfigConvert;
import com.shdatalink.sip.server.module.config.entity.UserStyleConfig;
import com.shdatalink.sip.server.module.config.service.UserStyleConfigService;
import com.shdatalink.sip.server.module.config.vo.UserStyleConfigSaveReq;
import com.shdatalink.sip.server.module.config.vo.UserStyleConfigVO;
import com.shdatalink.sip.server.module.user.vo.UserInfo;
import com.shdatalink.sip.server.utils.UserInfoUtil;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;

/**
 * 页面样式配置
 */
@RequiredArgsConstructor
@Path("admin/style/config")
public class UserStyleConfigController {

    @Inject
    UserStyleConfigService userStyleConfigService;

    @Inject
    UserStyleConfigConvert userStyleConfigConvert;


    /**
     * 获取配置
     */
    @GET
    @Path("get")
    public UserStyleConfigVO get() {
        UserInfo currentUser = UserInfoUtil.getUserInfoWithThrow();
        return userStyleConfigService.get(currentUser.getId());
    }

    /**
     * 保存配置
     */
    @POST
    @Path("save")
    public Boolean save(@Valid UserStyleConfigSaveReq saveReq) {
        UserInfo currentUser = UserInfoUtil.getUserInfoWithThrow();
        UserStyleConfig userStyleConfig = userStyleConfigService.getByUserId(currentUser.getId());
        if(userStyleConfig == null){
            userStyleConfig = new UserStyleConfig();
        }else{
            userStyleConfigConvert.updateEntity(saveReq, userStyleConfig);
        }
        userStyleConfig.setUserId(currentUser.getId());
        userStyleConfigService.saveOrUpdate(userStyleConfig);
        return true;
    }

    /**
     * 获取默认配置
     */
    @GET
    @Path("defaultConfig")
    public UserStyleConfigVO defaultConfig() {
        UserStyleConfig userStyleConfig = userStyleConfigService.getByUserId(0);
        return userStyleConfigConvert.toConfigVO(userStyleConfig);
    }
}
