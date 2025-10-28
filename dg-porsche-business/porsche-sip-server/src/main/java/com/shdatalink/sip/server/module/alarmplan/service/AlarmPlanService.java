package com.shdatalink.sip.server.module.alarmplan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.sip.server.common.dto.PageParam;
import com.shdatalink.sip.server.common.dto.PageParamWithGet;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify.AlarmSubscribe;
import com.shdatalink.sip.server.gb28181.core.builder.GBRequest;
import com.shdatalink.sip.server.module.alarmplan.entity.AlarmPlan;
import com.shdatalink.sip.server.module.alarmplan.entity.AlarmPlanChannelRel;
import com.shdatalink.sip.server.module.alarmplan.mapper.AlarmPlanMapper;
import com.shdatalink.sip.server.module.alarmplan.vo.AlarmPlanReq;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.mapper.DeviceMapper;
import com.shdatalink.sip.server.utils.SipUtil;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.alarmplan.service.AlarmPlanService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@ApplicationScoped
public class AlarmPlanService extends ServiceImpl<AlarmPlanMapper, AlarmPlan> {

    @Inject
    AlarmPlanChannelRelService alarmPlanChannelRelService;
    @Inject
    DeviceMapper deviceMapper;

    @Transactional(rollbackOn = Exception.class)
    public Boolean saveOrUpdate(AlarmPlanReq alarmPlanReq) {
        Integer id = alarmPlanReq.getId();
        AlarmPlan alarmPlan = new AlarmPlan();
        if (id != null) {
            alarmPlan = getOptById(id).orElseThrow(() -> new BizException("未找到报警预案信息"));
        }
        alarmPlan.setName(alarmPlanReq.getName());
        //新增
        alarmPlan.addAlarmPriorities(alarmPlanReq.getAlarmPriorities());
        alarmPlan.addAlarmMethods(alarmPlanReq.getAlarmMethods());
        alarmPlan.addAlarmTypes(alarmPlanReq.getAlarmTypes());
        alarmPlan.addEventTypes(alarmPlanReq.getEventTypes());
        alarmPlan.setStatus(alarmPlanReq.getStatus());
        boolean flag = saveOrUpdate(alarmPlan);
        //更新通道报警订阅
        updateChannelSubscribe(alarmPlan);
        return flag;
    }

    public void updateChannelSubscribe(AlarmPlan alarmPlan) {
        Integer alarmPlanId = alarmPlan.getId();
        List<String> channelIds = alarmPlanChannelRelService.getChannelIdsByPlanId(alarmPlanId);
        if(CollectionUtils.isEmpty(channelIds)){
            return;
        }
        for (String channelId : channelIds) {
            Device device = deviceMapper.selectByChannelId(channelId);
            AlarmSubscribe build = AlarmSubscribe.builder().sn(SipUtil.generateSn()).deviceId(channelId).startAlarmPriority("0").endAlarmPriority("0").alarmMethod("0").build();
            GBRequest.subscribe(device.toGbDevice(channelId))
                    .execute(build);
        }
    }

    public IPage<AlarmPlan> getPage(PageParamWithGet pageParam) {
        return page(new Page<>(pageParam.getPage(), pageParam.getPageSize()));
    }


    @Transactional(rollbackOn = Exception.class)
    public void delete(Integer planId) {
        baseMapper.deleteById(planId);
        List<String> channelIds = alarmPlanChannelRelService.getChannelIdsByPlanId(planId);
        for (String channelId : channelIds) {
            Device device = deviceMapper.selectByChannelId(channelId);
            GBRequest.subscribe(device.toGbDevice(channelId))
                    .execute(AlarmSubscribe.builder()
                            .sn(SipUtil.generateSn())
                            .deviceId(channelId)
                            .isSubscribe(false)
                            .build());
        }
        alarmPlanChannelRelService.remove(new LambdaQueryWrapper<AlarmPlanChannelRel>()
                .eq(AlarmPlanChannelRel::getAlarmPlanId, planId));
    }
}
