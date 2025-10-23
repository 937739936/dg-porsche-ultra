package com.shdatalink.sip.server.module.alarmplan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shdatalink.framework.json.utils.JsonUtil;
import com.shdatalink.framework.redis.utils.RedisUtil;
import com.shdatalink.sip.server.common.constants.RedisKeyConstants;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify.response.DeviceAlarm;
import com.shdatalink.sip.server.module.alarmplan.entity.AlarmRecord;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmMethodEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmPriorityEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmTypeEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.EventTypeEnum;
import com.shdatalink.sip.server.module.alarmplan.mapper.AlarmRecordMapper;
import com.shdatalink.sip.server.module.alarmplan.vo.AlarmRecordPageReq;
import com.shdatalink.sip.server.module.alarmplan.vo.AlarmRecordPageResp;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.mapper.DeviceChannelMapper;
import com.shdatalink.sip.server.module.device.service.DeviceSnapService;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewSnapshot;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.sip.server.module.alarmplan.service.AlarmRecordService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@ApplicationScoped
@Slf4j
public class AlarmRecordService extends ServiceImpl<AlarmRecordMapper, AlarmRecord> {

    @Inject
    DeviceChannelMapper deviceChannelMapper;
    @Inject
    RedisUtil redisUtil;
    @Inject
    DeviceSnapService deviceSnapService;

    public void handle(DeviceAlarm deviceAlarm) {
        String deviceAlarmCache = redisUtil.get(RedisKeyConstants.ALARM_NOTIFY + deviceAlarm.getSn());
        if(StringUtils.isNotBlank(deviceAlarmCache)){
            log.info("重复报警，deviceAlarm:{}", JsonUtil.toJsonString(deviceAlarm));
            return;
        }
        redisUtil.set(RedisKeyConstants.ALARM_NOTIFY + deviceAlarm.getSn(), JsonUtil.toJsonString(deviceAlarm));
        redisUtil.expire(RedisKeyConstants.ALARM_NOTIFY + deviceAlarm.getSn(), Duration.ofSeconds(3));

        AlarmRecord alarmRecord = new AlarmRecord();
        DeviceChannel deviceChannel = deviceChannelMapper.selectByChannelId(deviceAlarm.getDeviceId());
        if (deviceChannel != null) {
            alarmRecord.setDeviceId(deviceChannel.getDeviceId());
        }
        alarmRecord.setSn(deviceAlarm.getSn());
        alarmRecord.setChannelId(deviceAlarm.getDeviceId());
        alarmRecord.setAlarmPriority(AlarmPriorityEnum.fromVal(deviceAlarm.getAlarmPriority()));
        alarmRecord.setAlarmMethod(AlarmMethodEnum.fromVal(deviceAlarm.getAlarmMethod()));
        LocalDateTime alarmTime = LocalDateTime.parse(deviceAlarm.getAlarmTime());
        alarmRecord.setAlarmTime(alarmTime);
        if(StringUtils.isBlank(deviceAlarm.getAlarmType()) && deviceAlarm.getInfo() != null){
            DeviceAlarm.AlarmInfo info = deviceAlarm.getInfo();
            alarmRecord.setAlarmType(AlarmTypeEnum.fromVal(info.getAlarmType(), alarmRecord.getAlarmMethod()));
            if (info.getAlarmTypeParam() != null) {
                alarmRecord.setEventType(EventTypeEnum.fromVal(info.getAlarmTypeParam().getEventType()));
            }
        }else{
            alarmRecord.setAlarmType(AlarmTypeEnum.fromVal(deviceAlarm.getAlarmType(), alarmRecord.getAlarmMethod()));
        }

        if (deviceChannel != null) {
            Path snapPath = deviceSnapService.getSnapPath(deviceChannel.getDeviceId(), deviceChannel.getChannelId());
            Path fileName = snapPath.getParent().resolve(deviceAlarm.getSn()).resolve(snapPath.getFileName());
            deviceSnapService.snapshot(deviceChannel.getDeviceId(), deviceChannel.getChannelId(), fileName);
            alarmRecord.setFilePath(fileName.toString());
        }

        //生成快照
        save(alarmRecord);
    }

    public IPage<AlarmRecordPageResp> getPage(AlarmRecordPageReq pageReq) {
        return baseMapper.selectPage(
                new Page<>(pageReq.getPage(), pageReq.getPageSize()),
                new LambdaQueryWrapper<AlarmRecord>()
                        .eq(StringUtils.isNotBlank(pageReq.getDeviceId()),AlarmRecord::getDeviceId, pageReq.getDeviceId())
                        .ge(pageReq.getStartTime() != null, AlarmRecord::getAlarmTime, pageReq.getStartTime())
                        .le(pageReq.getEndTime() != null, AlarmRecord::getAlarmTime, pageReq.getEndTime())
                        .eq(pageReq.getAlarmPriority() != null, AlarmRecord::getAlarmPriority, pageReq.getAlarmPriority())
                        .eq(pageReq.getAlarmMethod() != null, AlarmRecord::getAlarmMethod, pageReq.getAlarmMethod())
                        .orderByDesc(AlarmRecord::getCreatedTime)
        ).convert(item -> {
            AlarmRecordPageResp page = new AlarmRecordPageResp();
            page.setId(item.getId());
            page.setDeviceId(item.getDeviceId());
            page.setChannelId(item.getChannelId());
            page.setAlarmMethod(item.getAlarmMethod().getText());
            page.setAlarmPriority(item.getAlarmPriority().getText());
            page.setAlarmTime(item.getAlarmTime());
            page.setAlarmType(item.getAlarmType().getText());
            page.setEventType(item.getEventType() == null ? "" : item.getEventType().getText());

            Path snapPath = deviceSnapService.getSnapPath(item.getDeviceId(), item.getChannelId());
            Path fileName = snapPath.getParent().resolve(item.getSn()).resolve(snapPath.getFileName());
            DevicePreviewSnapshot snapshot = deviceSnapService.querySnapshot(fileName);
            page.setBase64(snapshot.getBase64());
            return page;
        });
    }

    public boolean batchDeleteRecord(List<Integer> idList) {
        int i = baseMapper.deleteByIds(idList);
        return i > 0;
    }
}
