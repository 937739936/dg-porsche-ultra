package com.shdatalink.sip.server.module.device.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shdatalink.sip.server.module.device.entity.DeviceLog;
import com.shdatalink.sip.server.module.device.enums.MessageTypeEnum;
import com.shdatalink.sip.server.module.device.event.DeviceRegisterEvent;
import com.shdatalink.sip.server.module.device.mapper.DeviceLogMapper;
import com.shdatalink.sip.server.module.device.vo.DeviceLogsPage;
import com.shdatalink.sip.server.module.device.vo.DeviceLogsPageParam;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.device.service.DeviceLogService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@Slf4j
@ApplicationScoped
public class DeviceLogService extends ServiceImpl<DeviceLogMapper, DeviceLog> {

    public void handleDeviceRegisterEvent(@ObservesAsync DeviceRegisterEvent event) {
        String content = "";
        if (event.getType()) {
            switch (event.getStatus()) {
                case Fail ->  content = "设备注册失败";
                case Success -> content = "设备注册成功";
            }
        } else {
            switch (event.getStatus()) {
                case Fail ->  content = "设备注销失败";
                case Success -> content = "设备注销成功";
            }
        }
        addLog(event.getDeviceId(), event.getType(), MessageTypeEnum.Register, content);
    }

    public void addLog(String deviceId, Boolean online, MessageTypeEnum type, String content) {
        addLog(deviceId, null, online, type, content);
    }
    public void addLog(String deviceId, String channelId, Boolean online, MessageTypeEnum type, String content) {
        DeviceLog log = new DeviceLog();
        log.setDeviceId(deviceId);
        log.setChannelId(channelId);
        log.setType(type);
        log.setContent(content);
        log.setOnline(online);
        save(log);
    }

    public IPage<DeviceLogsPage> getPage(DeviceLogsPageParam param) {
        LambdaQueryWrapper<DeviceLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceLog::getDeviceId, param.getDeviceId())
                .eq(StringUtils.isNotBlank(param.getChannelId()), DeviceLog::getChannelId, param.getChannelId())
                .isNull(StringUtils.isBlank(param.getChannelId()), DeviceLog::getChannelId)
                .orderByDesc(DeviceLog::getCreatedTime);
        Page<DeviceLog> page = page(new Page<>(param.getPage(), param.getPageSize()), wrapper);
        return page.convert(p -> {
            DeviceLogsPage logs = new DeviceLogsPage();
            logs.setContent(p.getContent());
            logs.setCreateTime(p.getCreatedTime());
            logs.setOnline(p.getOnline());
            return logs;
        });
    }
}
