package com.shdatalink.sip.server.module.config.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.shdatalink.sip.server.module.config.convert.UserStyleConfigConvert;
import com.shdatalink.sip.server.module.config.entity.UserStyleConfig;
import com.shdatalink.sip.server.module.config.mapper.UserStyleConfigMapper;
import com.shdatalink.sip.server.module.config.vo.UserStyleConfigVO;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.config.service.UserStyleConfigService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@ApplicationScoped
public class UserStyleConfigService extends ServiceImpl<UserStyleConfigMapper, UserStyleConfig> {

    @Inject
    UserStyleConfigConvert userStyleConfigConvert;

    public UserStyleConfig getByUserId(Integer userId) {
        return baseMapper.selectOne(new LambdaQueryWrapper<UserStyleConfig>().eq(UserStyleConfig::getUserId, userId));
    }

    public UserStyleConfigVO get(Integer userId) {
        UserStyleConfig userStyleConfig = getByUserId(userId);
        if(userStyleConfig == null){
            userStyleConfig = getByUserId(0);
        }
        return userStyleConfigConvert.toConfigVO(userStyleConfig);
    }


}
