package com.shdatalink.sip.server.module.alarmplan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shdatalink.framework.json.utils.JsonUtil;
import com.shdatalink.framework.redis.utils.RedisUtil;
import com.shdatalink.sip.server.common.constants.RedisKeyConstants;
import com.shdatalink.sip.server.config.SipConfigProperties;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify.response.DeviceAlarm;
import com.shdatalink.sip.server.media.MediaHttpClient;
import com.shdatalink.sip.server.media.MediaService;
import com.shdatalink.sip.server.media.bean.entity.req.SnapshotReq;
import com.shdatalink.sip.server.module.alarmplan.entity.AlarmRecord;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmMethodEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmPriorityEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmTypeEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.EventTypeEnum;
import com.shdatalink.sip.server.module.alarmplan.mapper.AlarmRecordMapper;
import com.shdatalink.sip.server.module.alarmplan.vo.AlarmRecordPageReq;
import com.shdatalink.sip.server.module.alarmplan.vo.AlarmRecordPageResp;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import com.shdatalink.sip.server.module.device.mapper.DeviceChannelMapper;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@ApplicationScoped
@Slf4j
public class AlarmRecordService extends ServiceImpl<AlarmRecordMapper, AlarmRecord> {

    @Inject
    DeviceChannelMapper deviceChannelMapper;
    @Inject
    @RestClient
    MediaHttpClient mediaHttpClient;
    @Inject
    SipConfigProperties sipConfigProperties;
    @Inject
    RedisUtil redisUtil;
    @Inject
    MediaService mediaService;

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

        //生成快照
        alarmRecord.setFilePath(generateSnapshot(deviceAlarm.getSn(), deviceChannel));
        save(alarmRecord);
    }

    public String generateSnapshot(String sn, DeviceChannel deviceChannel) {
        if (deviceChannel == null) {
            return null;
        }
        try {
            String rtspUrl = mediaService.getSnapshotUrl(ProtocolTypeEnum.GB28181, deviceChannel.getId());
            SnapshotReq req = new SnapshotReq();
            req.setUrl(rtspUrl);
            req.setTimeoutSec(5);
            req.setExpireSec(5);
            long startTime = System.currentTimeMillis();
            byte[] snap = mediaHttpClient.getSnap(req);
            long endTime = System.currentTimeMillis();
            log.info("截图成功，耗时：{}ms，deviceId: {}, channelId: {}, rtspUrl: {}", (endTime - startTime), deviceChannel.getDeviceId(), deviceChannel.getChannelId(), rtspUrl);
            String snapPath = sipConfigProperties.media().snapPath();
            String folderPath = snapPath + File.separator + deviceChannel.getDeviceId() + "_" + deviceChannel.getChannelId() + File.separator + sn;
            if (!Files.exists(Paths.get(folderPath))) {
                Files.createDirectories(Paths.get(folderPath));
            }
            String filePath = folderPath + File.separator + deviceChannel.getDeviceId() + "_" + deviceChannel.getChannelId() + ".jpg";
            Files.write(Paths.get(filePath), snap);
            return filePath;
        } catch (Exception e) {
            log.error("截图失败，", e);
        }
        return null;
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
            String snapPath = sipConfigProperties.media().snapPath();;
            String folderPath = snapPath + File.separator + item.getDeviceId() + "_" + item.getChannelId() + File.separator + item.getSn();

            Path path = Paths.get(folderPath, item.getDeviceId() + "_" + item.getChannelId() + ".jpg");
            if (Files.exists(path)) {
                try {
                    byte[] bytes = Files.readAllBytes(path);
                    page.setBase64(Base64.getEncoder().encodeToString(bytes));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return page;
        });
    }

    public boolean batchDeleteRecord(List<Integer> idList) {
        int i = baseMapper.deleteByIds(idList);
        return i > 0;
    }
}
