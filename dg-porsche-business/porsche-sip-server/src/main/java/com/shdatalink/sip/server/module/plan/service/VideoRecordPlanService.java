package com.shdatalink.sip.server.module.plan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.framework.common.service.EventPublisher;
import com.shdatalink.sip.server.module.plan.convert.VideoRecordConvert;
import com.shdatalink.sip.server.module.plan.entity.VideoRecordDevice;
import com.shdatalink.sip.server.module.plan.entity.VideoRecordPlan;
import com.shdatalink.sip.server.module.plan.event.PlanModifyEvent;
import com.shdatalink.sip.server.module.plan.mapper.VideoRecordPlanMapper;
import com.shdatalink.sip.server.module.plan.vo.VideoPlanPageParam;
import com.shdatalink.sip.server.module.plan.vo.VideoRecordPlanDetailVO;
import com.shdatalink.sip.server.module.plan.vo.VideoRecordPlanPage;
import com.shdatalink.sip.server.module.plan.vo.VideoRecordPlanSaveParam;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.plan.service.VideoRecordPlanService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@ApplicationScoped
public class VideoRecordPlanService extends ServiceImpl<VideoRecordPlanMapper, VideoRecordPlan> {
    @Inject
    EventPublisher publisher;
    @Inject
    VideoRecordConvert videoRecordConvert;

    public IPage<VideoRecordPlanPage> getPage(VideoPlanPageParam param) {
        return baseMapper.selectPage(
                new Page<>(param.getPage(), param.getPageSize()),
                new LambdaQueryWrapper<VideoRecordPlan>()
                        .like(StringUtils.isNotBlank(param.getName()), VideoRecordPlan::getName, param.getName())
                        .orderByDesc(VideoRecordPlan::getId)
        ).convert(item -> videoRecordConvert.to(item));
    }

    @Transactional
    public boolean enableSwitch(Integer id, Boolean enabled) {
        VideoRecordPlan plan = getOptById(id).orElseThrow(() -> new BizException("录像计划不存在"));
        plan.setEnabled(enabled);
        updateById(plan);
        publisher.fireAsyncAfterCommit(new PlanModifyEvent());
        return true;
    }

    @Transactional
    public boolean save(VideoRecordPlanSaveParam param) {
        VideoRecordPlan plan;
        if (param.getId() == null) {
            plan = new VideoRecordPlan();
            plan.setEnabled(true);
        } else {
            plan = getOptById(param.getId()).orElseThrow(() -> new BizException("录像计划不存在"));
        }
        plan.setName(param.getName());
        plan.setMonday(Integer.parseInt(param.getMonday(), 2));
        plan.setTuesday(Integer.parseInt(param.getTuesday(), 2));
        plan.setWednesday(Integer.parseInt(param.getWednesday(), 2));
        plan.setThursday(Integer.parseInt(param.getThursday(), 2));
        plan.setFriday(Integer.parseInt(param.getFriday(), 2));
        plan.setSaturday(Integer.parseInt(param.getSaturday(), 2));
        plan.setSunday(Integer.parseInt(param.getSunday(), 2));
        saveOrUpdate(plan);

        publisher.fireAsyncAfterCommit(new PlanModifyEvent());
        return true;
    }

    public List<VideoRecordPlan> getPlanOfNow(String weekDay, Integer hour) {
        if (hour < 0 || hour > 23) {
            throw new RuntimeException("小时设置错误，为0-23");
        }
        int flag = 1 << (23-hour);
        return baseMapper.selectByWeekDayAndFlag(weekDay, flag);
    }

    // 查找某个小时要录像的设备
    public List<VideoRecordDevice> getPlanByChannelOfNow(String weekDay, String deviceId, String channelId, Integer hour) {
        if (hour < 0 || hour > 23) {
            throw new RuntimeException("小时设置错误，为0-23");
        }
        int flag = 1 << (23-hour);
        return baseMapper.selectDeviceByWeekDayAndFlag(weekDay, deviceId, channelId, flag);
    }

    private String toBitString(int value) {
        String bitStr = Integer.toBinaryString(value);
        return String.format("%24s", bitStr).replace(' ', '0');
    }

    public VideoRecordPlanDetailVO detail(Integer id) {
        VideoRecordPlan plan = getOptById(id).orElseThrow(() -> new BizException("录像计划不存在"));
        VideoRecordPlanDetailVO vo = new VideoRecordPlanDetailVO();
        vo.setId(plan.getId());
        vo.setName(plan.getName());
        vo.setMonday(toBitString(plan.getMonday()));
        vo.setTuesday(toBitString(plan.getTuesday()));
        vo.setWednesday(toBitString(plan.getWednesday()));
        vo.setThursday(toBitString(plan.getThursday()));
        vo.setFriday(toBitString(plan.getFriday()));
        vo.setSaturday(toBitString(plan.getSaturday()));
        vo.setSunday(toBitString(plan.getSunday()));
        return vo;
    }
}
