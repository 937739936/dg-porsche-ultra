package com.shdatalink.sip.server.module.common.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.shdatalink.framework.json.utils.JsonUtil;
import com.shdatalink.sip.server.module.common.entity.OperateLog;
import com.shdatalink.sip.server.module.common.enums.OperateLogTypeEnum;
import com.shdatalink.sip.server.module.common.mapper.OperateLogMapper;
import com.shdatalink.framework.web.utils.IpUtil;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.http.HttpServerRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.common.service.OperateLogService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@Slf4j
@ApplicationScoped
public class OperateLogService extends ServiceImpl<OperateLogMapper, OperateLog> {

    @Inject
    HttpServerRequest request;

    public void addLog(String logContent, OperateLogTypeEnum operateType, Integer userId, String fullName) {
        OperateLog operateLog = new OperateLog();
        //注解上的描述,操作日志内容
        operateLog.setLogContent(logContent);
        operateLog.setOperateType(operateType);
        try {
            operateLog.setMethod(request.uri());
            operateLog.setRequestParam(JsonUtil.toJsonString(request.params()));
            operateLog.setRequestType(request.method().name());
            //设置IP地址
            operateLog.setIp(IpUtil.getIpAddr());
        } catch (Exception e) {
            operateLog.setIp("127.0.0.1");
        }

        //获取登录用户信息
        operateLog.setUsername(fullName);
        //保存系统日志
        baseMapper.insert(operateLog);
    }

}
