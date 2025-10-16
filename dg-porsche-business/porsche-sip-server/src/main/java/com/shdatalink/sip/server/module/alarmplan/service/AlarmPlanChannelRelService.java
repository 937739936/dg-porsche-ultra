package com.shdatalink.sip.server.module.alarmplan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.sip.server.module.alarmplan.entity.AlarmPlanChannelRel;
import com.shdatalink.sip.server.module.alarmplan.mapper.AlarmPlanChannelRelMapper;
import com.shdatalink.sip.server.module.alarmplan.vo.AlarmPlanChannelSaveReq;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.mapper.DeviceChannelMapper;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@ApplicationScoped
public class AlarmPlanChannelRelService extends ServiceImpl<AlarmPlanChannelRelMapper, AlarmPlanChannelRel> {

    @Inject
    DeviceChannelMapper deviceChannelMapper;

    public List<String> getChannelIdsByPlanId(Integer planId) {
        return getChannelByPlanId(planId).stream().map(AlarmPlanChannelRel::getChannelId).toList();
    }

    public List<AlarmPlanChannelRel> getChannels(Integer planId) {
        return getChannelByPlanId(planId);
    }

    public List<AlarmPlanChannelRel> getChannelByPlanId(Integer planId) {
        return baseMapper.selectList(new LambdaQueryWrapper<AlarmPlanChannelRel>().eq(AlarmPlanChannelRel::getAlarmPlanId, planId));
    }

    @Transactional(rollbackOn = Exception.class)
    public void savePlanChannels(AlarmPlanChannelSaveReq saveReq) {
        Integer planId = saveReq.getPlanId();
        List<String> newChannelIds = saveReq.getChannelIds();
        baseMapper.delete(new LambdaQueryWrapper<AlarmPlanChannelRel>().eq(AlarmPlanChannelRel::getAlarmPlanId, planId));
        if (CollectionUtils.isNotEmpty(newChannelIds)) {
            List<AlarmPlanChannelRel> insertList = newChannelIds.stream().distinct().map(channelId -> {
                DeviceChannel deviceChannel = deviceChannelMapper.selectByChannelId(channelId);
                if(deviceChannel == null){
                    throw new BizException("未找到对应的通道信息，channelId：" + channelId);
                }
                AlarmPlanChannelRel planChannelRel = new AlarmPlanChannelRel();
                planChannelRel.setAlarmPlanId(planId);
                planChannelRel.setDeviceId(deviceChannel.getDeviceId());
                planChannelRel.setChannelId(channelId);
                return planChannelRel;
            }).toList();
            baseMapper.insert(insertList);
        }
    }


}
