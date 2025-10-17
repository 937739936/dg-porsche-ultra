package com.shdatalink.sip.server.module.plan.schedue;

import com.shdatalink.sip.server.gb28181.StreamFactory;
import com.shdatalink.sip.server.gb28181.core.bean.constants.TransportTypeEnum;
import com.shdatalink.sip.server.gb28181.core.builder.GBRequest;
import com.shdatalink.sip.server.media.MediaHttpClient;
import com.shdatalink.sip.server.media.MediaService;
import com.shdatalink.sip.server.media.bean.entity.req.MediaReq;
import com.shdatalink.sip.server.media.bean.entity.req.StartRecordReq;
import com.shdatalink.sip.server.media.bean.entity.resp.IsRecordingResult;
import com.shdatalink.sip.server.media.bean.entity.resp.MediaServerResponse;
import com.shdatalink.sip.server.media.event.MediaExitedEvent;
import com.shdatalink.sip.server.media.event.MediaRegisterEvent;
import com.shdatalink.sip.server.module.config.enums.ConfigTypesEnum;
import com.shdatalink.sip.server.module.config.service.ConfigService;
import com.shdatalink.sip.server.module.config.vo.VideoRecordConfig;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import com.shdatalink.sip.server.module.device.event.DeviceOnlineEvent;
import com.shdatalink.sip.server.module.device.mapper.DeviceChannelMapper;
import com.shdatalink.sip.server.module.device.mapper.DeviceMapper;
import com.shdatalink.sip.server.module.device.service.DeviceChannelService;
import com.shdatalink.sip.server.module.plan.entity.VideoRecordDevice;
import com.shdatalink.sip.server.module.plan.event.PlanModifyEvent;
import com.shdatalink.sip.server.module.plan.service.VideoRecordPlanService;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

@Slf4j
@Singleton
@Startup
public class VideoRecordSchedule {

    @Inject
    @RestClient
    MediaHttpClient mediaHttpClient;

    @Inject
    VideoRecordPlanService videoRecordPlanService;
    @Inject
    DeviceChannelMapper deviceChannelMapper;
    @Inject
    DeviceMapper deviceMapper;
    @Inject
    DeviceChannelService deviceChannelService;
    @Inject
    Executor executor;
    @Inject
    MediaService mediaService;
    @Inject
    ConfigService configService;

    public void init(@ObservesAsync StartupEvent event) {
        try {
            recordSchedule();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPlanModify(@ObservesAsync PlanModifyEvent event) {
        recordSchedule();
    }

    public void mediaRegister(@ObservesAsync MediaRegisterEvent event) {
        recordSchedule();
    }

    public void mediaExited(@ObservesAsync MediaExitedEvent event) {
        deviceChannelService.selectRegisterChannel()
                .forEach(channel -> {
                    deviceChannelService.updateRecording(channel.getId(), false);
                });
    }

    public void channelOnlineStatus(@ObservesAsync DeviceOnlineEvent event) {
        // 设备状态变更
        if (event.getChannelId() == null) {
            Device device = deviceMapper.selectByDeviceId(event.getDeviceId());
            if (event.getOnline()) {
                startRecordByDevice(device);
            } else {
                stopRecordByDevice(device);
            }
        } else {
            deviceChannelService.findByDeviceIdAndChannelId(event.getDeviceId(), event.getChannelId())
                    .ifPresent(channel -> {
                        if (event.getOnline()) {
                            startRecordByChannels(Collections.singletonList(channel));
                        } else {
                            stopRecordByChannels(Collections.singletonList(channel));
                        }
                    });
        }
    }

    /**
     * 开始/停止录像，每小时执行一次
     */
    @Scheduled(cron = "5 0 */1 * * ?")
    @RunOnVirtualThread
    public void recordSchedule() {
        startRecordByChannels(deviceChannelService.selectRegisterChannel());
        stopRecordByChannels(deviceChannelMapper.selectList(null));
    }

    public void startRecordByDevice(Device device) {
        startRecordByChannels(deviceChannelService.getBaseMapper().selectByDeviceId(device.getDeviceId()));
    }

    public void startRecordByChannels(List<DeviceChannel> channels) {
        VideoRecordConfig config = configService.getConfig(ConfigTypesEnum.VideoRecord);

        String weekDay = LocalDate.now().getDayOfWeek().name().toLowerCase();
        for (DeviceChannel channel : channels) {
            if (!channel.getOnline()) continue;
//            // 查找录像计划中是否有当前小时开启的录像计划
            List<VideoRecordDevice> devices = videoRecordPlanService.getPlanByChannelOfNow(weekDay, channel.getDeviceId(), channel.getChannelId(), LocalDateTime.now().getHour());
            if (!devices.isEmpty()) {
                Device device = deviceMapper.selectByChannelId(channel.getChannelId());
                if (device == null) {
                    continue;
                }
                String streamId = StreamFactory.liveStreamId(device.getProtocolType(), channel.getId().toString());
                IsRecordingResult recording = mediaHttpClient.isRecording(new MediaReq(streamId));
                // 没有流的时候要打开流
                if (recording.getCode() == -500) {
                    if (device.getProtocolType() == ProtocolTypeEnum.GB28181) {
                        deviceChannelService.play(streamId);
                    } else if (device.getProtocolType() == ProtocolTypeEnum.PULL) {
                        Boolean online = mediaService.addPullStream(device.getStreamUrl(), streamId, TransportTypeEnum.parse(device.getTransport()), true);
                        if (online == null || !online) {
                            log.error("拉流失败，设备ID：{}，通道ID：{}", device.getDeviceId(), channel.getChannelId());
                        }
                    }
                } else if (recording.getCode() == 0 && recording.getStatus()) {
                    deviceChannelService.updateRecording(channel.getId(), true);
                    continue;
                }

                executor.execute(() -> {
                    if (device.getProtocolType() == ProtocolTypeEnum.GB28181) {
                        while (!mediaService.rtpServerExists(streamId)) {
                            StartRecordReq startRecordReq = new StartRecordReq();
                            startRecordReq.setType(1);
                            startRecordReq.setMaxSecond(60);
                            startRecordReq.setStream(streamId);
                            MediaServerResponse<Void> mediaServerResponse = mediaHttpClient.startRecord(startRecordReq);
                            if (mediaServerResponse.getCode() == 0) {
                                deviceChannelService.updateRecording(channel.getId(), true);
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else if (device.getProtocolType() == ProtocolTypeEnum.PULL || device.getProtocolType() == ProtocolTypeEnum.RTMP) {
                        StartRecordReq startRecordReq = new StartRecordReq();
                        startRecordReq.setType(1);
                        startRecordReq.setMaxSecond(60);
                        startRecordReq.setCustomizedPath(config.getStorePath());
                        startRecordReq.setStream(streamId);
                        MediaServerResponse<Void> mediaServerResponse = mediaHttpClient.startRecord(startRecordReq);
                        if (mediaServerResponse.getCode() == 0) {
                            deviceChannelService.updateRecording(channel.getId(), true);
                            return;
                        }
                    }
                });
            }
        }
    }

    public void stopRecordByDevice(Device device) {
        stopRecordByChannels(deviceChannelService.getBaseMapper().selectByDeviceId(device.getDeviceId()));
    }

    public void stopRecordByChannels(List<DeviceChannel> channels) {
        String weekDay = LocalDate.now().getDayOfWeek().name().toLowerCase();
        for (DeviceChannel channel : channels) {
            if (!channel.getRecording()) {
                continue;
            }
            Device device = deviceMapper.selectByDeviceId(channel.getDeviceId());
            String streamId = StreamFactory.liveStreamId(device.getProtocolType(), channel.getId().toString());
            MediaReq mediaReq = new MediaReq();
            mediaReq.setStream(streamId);
            IsRecordingResult recording = mediaHttpClient.isRecording(mediaReq);
            // 流不存在，也就没有在录像
            if (recording.getCode() == -500) {
                deviceChannelService.updateRecording(channel.getId(), false);
                continue;
            }
            if (recording.getCode() == 0 && recording.getStatus()) {
                // 如果正在录像，就查找录像计划中是否有当前小时开启的录像计划
                List<VideoRecordDevice> devices = videoRecordPlanService.getPlanByChannelOfNow(weekDay, channel.getDeviceId(), channel.getChannelId(), LocalDateTime.now().getHour());
                if (devices.isEmpty() || !channel.getOnline()) {
                    // 如果没有，就停止
                    mediaHttpClient.stopRecord(new MediaReq(streamId));
                    deviceChannelService.updateRecording(channel.getId(), false);
                    if (!mediaService.streamReaderExists(streamId)) {
                        if (device.getProtocolType() == ProtocolTypeEnum.GB28181) {
                            GBRequest.bye(device.toGbDevice(channel.getChannelId())).withStreamId(streamId).execute();
                        }
                        mediaService.closeStreams(streamId);
                    }
                }
            }
        }
    }
}
