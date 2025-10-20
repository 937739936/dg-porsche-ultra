package com.shdatalink.sip.server.module.alarmplan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify.AlarmSubscribe;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify.CatalogSubscribe;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify.MobilePositionSubscribe;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify.response.ResponseMessage;
import com.shdatalink.sip.server.gb28181.core.builder.GBRequest;
import com.shdatalink.sip.server.module.alarmplan.convert.AlarmPlanConvert;
import com.shdatalink.sip.server.module.alarmplan.entity.Subscribe;
import com.shdatalink.sip.server.module.alarmplan.enums.SubscribeTypeEnum;
import com.shdatalink.sip.server.module.alarmplan.mapper.SubscribeMapper;
import com.shdatalink.sip.server.module.alarmplan.vo.SubscribeReq;
import com.shdatalink.sip.server.module.alarmplan.vo.SubscribeResp;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.mapper.DeviceMapper;
import com.shdatalink.sip.server.utils.SipUtil;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.alarmplan.service.SubscribeService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@ApplicationScoped
public class SubscribeService extends ServiceImpl<SubscribeMapper, Subscribe> {

    @Inject
    DeviceMapper deviceMapper;
    @Inject
    AlarmPlanConvert alarmPlanConvert;

    @Transactional(rollbackOn = Exception.class)
    public void save(SubscribeReq subscribeReq) {
        String deviceId = subscribeReq.getDeviceId();
        int saveUpdateCount = baseMapper.selectCount(new LambdaQueryWrapper<Subscribe>().eq(Subscribe::getDeviceId, deviceId)) > 0 ?
                baseMapper.update(new Subscribe() {{
                    setCatalog(subscribeReq.isCatalog());
                    setAlarm(subscribeReq.isCatalog());
                    setPosition(subscribeReq.isPosition());
                }}, new LambdaQueryWrapper<Subscribe>().eq(Subscribe::getDeviceId, deviceId))
                :
                baseMapper.insert(new Subscribe() {{
                    setDeviceId(deviceId);
                    setCatalog(subscribeReq.isCatalog());
                    setAlarm(subscribeReq.isAlarm());
                    setPosition(subscribeReq.isPosition());
                }});
        if (saveUpdateCount <= 0) {
            throw new BizException("保存订阅信息失败");
        }
        //事件订阅
        Device device = deviceMapper.selectByDeviceId(deviceId);
        if (device == null) {
            throw new BizException("设备信息不存在或已删除");
        }
        catalogSubscribe(device, subscribeReq.isCatalog());
        alarmSubscribe(device, subscribeReq.isAlarm());
        positionSubscribe(device, subscribeReq.isPosition());
    }

    public void alarmSubscribe(Device device, boolean alarm) {
        GBRequest.subscribe(device.toGbDevice())
                .newSession()
                .execute(AlarmSubscribe.builder()
                        .sn(SipUtil.generateSn())
                        .deviceId(device.getDeviceId())
                        .startAlarmPriority("0")
                        .endAlarmPriority("0")
                        .alarmMethod("0")
                        .isSubscribe(alarm)
                        .build());
    }

    public void catalogSubscribe(Device device, boolean isCatalog) {
        GBRequest.subscribe(device.toGbDevice())
                .newSession()
                .execute(CatalogSubscribe.builder()
                        .sn(SipUtil.generateSn())
                        .deviceId(device.getDeviceId())
                        .isSubscribe(isCatalog)
                        .build());
    }

    public void positionSubscribe(Device device, boolean isPosition) {
        GBRequest.subscribe(device.toGbDevice())
                .newSession()
                .execute(MobilePositionSubscribe.builder()
                        .sn(SipUtil.generateSn())
                        .deviceId(device.getDeviceId())
                        .Interval(10)
                        .isSubscribe(isPosition)
                        .build());
    }

    public void updateSubscribeExpires(ResponseMessage responseMessage, int expires) {
        String deviceId = responseMessage.getDeviceId();
        //是否是有效的
        boolean isUnexpired = expires > 0;
        LocalDateTime expireTime = isUnexpired ? LocalDateTime.now().plusSeconds(expires) : null;
        if (SubscribeTypeEnum.CATALOG.getCode().equalsIgnoreCase(responseMessage.getCmdType())) {
            Subscribe subscribe = baseMapper.selectOne(new LambdaQueryWrapper<Subscribe>().eq(Subscribe::getDeviceId, deviceId));
            int i = subscribe == null ?
                    baseMapper.insert(new Subscribe() {{
                        setDeviceId(deviceId);
                        setCatalog(isUnexpired);
                        setCatalogTime(expireTime);
                    }})
                    :
                    baseMapper.update(new LambdaUpdateWrapper<Subscribe>().eq(Subscribe::getDeviceId, deviceId).set(Subscribe::getCatalogTime, expireTime).set(Subscribe::isCatalog, isUnexpired));
            if (i <= 0) {
                throw new BizException("保存订阅信息失败");
            }
        } else if (SubscribeTypeEnum.MOBILE_POSITION.getCode().equalsIgnoreCase(responseMessage.getCmdType())) {
            Subscribe subscribe = baseMapper.selectOne(new LambdaQueryWrapper<Subscribe>().eq(Subscribe::getDeviceId, deviceId));
            int i = subscribe == null ?
                    baseMapper.insert(new Subscribe() {{
                        setDeviceId(deviceId);
                        setPosition(isUnexpired);
                        setPositionTime(expireTime);
                    }})
                    :
                    baseMapper.update(new LambdaUpdateWrapper<Subscribe>().eq(Subscribe::getDeviceId, deviceId).set(Subscribe::getPositionTime, expireTime).set(Subscribe::isPosition, isUnexpired));
            if (i <= 0) {
                throw new BizException("保存订阅信息失败");
            }
        }else if (SubscribeTypeEnum.ALARM.getCode().equalsIgnoreCase(responseMessage.getCmdType())) {
            Subscribe subscribe = baseMapper.selectOne(new LambdaQueryWrapper<Subscribe>().eq(Subscribe::getDeviceId, deviceId));
            int i = subscribe == null ?
                    baseMapper.insert(new Subscribe() {{
                        setDeviceId(deviceId);
                        setAlarm(isUnexpired);
                        setAlarmTime(expireTime);
                    }})
                    :
                    baseMapper.update(new LambdaUpdateWrapper<Subscribe>().eq(Subscribe::getDeviceId, deviceId).set(Subscribe::getAlarmTime, expireTime).set(Subscribe::isAlarm, isUnexpired));
            if (i <= 0) {
                throw new BizException("保存订阅信息失败");
            }
        }
    }

    public SubscribeResp info(String deviceId) {
        Subscribe subscribe = baseMapper.selectOne(new LambdaQueryWrapper<Subscribe>().eq(Subscribe::getDeviceId, deviceId).last("limit 1"));
        SubscribeResp subscribeResp = new SubscribeResp();
        if(subscribe == null){
            subscribeResp.setCatalog(false);
            subscribeResp.setAlarm(false);
            subscribeResp.setPosition(false);
            return subscribeResp;
        }
        return alarmPlanConvert.convert(subscribe);
    }
}
