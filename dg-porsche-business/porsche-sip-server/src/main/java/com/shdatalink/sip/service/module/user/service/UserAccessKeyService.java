package com.shdatalink.sip.service.module.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.sip.service.module.user.entity.UserAccessKey;
import com.shdatalink.sip.service.module.user.mapper.UserAccessKeyMapper;
import com.shdatalink.sip.service.utils.UserInfoUtil;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.service.module.user.service.UserAccessKeyService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@Slf4j
@ApplicationScoped
public class UserAccessKeyService extends ServiceImpl<UserAccessKeyMapper, UserAccessKey> {
    public UserAccessKey generateAccessKey() {
        Integer userId = UserInfoUtil.getUserId();
        String key = RandomStringUtils.secure().nextAlphanumeric(10);
        UserAccessKey userAccessKey = new UserAccessKey();
        userAccessKey.setUserId(userId);
        userAccessKey.setAccessKey(key);
        userAccessKey.setSecret(RandomStringUtils.secure().nextAlphanumeric(32));
        try {
            this.save(userAccessKey);
        } catch (org.apache.ibatis.exceptions.PersistenceException e) {
            throw new BizException("key生成失败请重试");
        }
        return userAccessKey;
    }

    public List<UserAccessKey> getUserAccessKeys(String userId) {
        return baseMapper.selectList(new LambdaQueryWrapper<UserAccessKey>().eq(UserAccessKey::getUserId, userId));
    }

    public UserAccessKey getUserAccessKey(String accessKey) {
        return baseMapper.selectOne(new LambdaQueryWrapper<UserAccessKey>().eq(UserAccessKey::getAccessKey, accessKey));
    }
}
