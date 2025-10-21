package com.shdatalink.sip.server.media.hook.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.framework.common.service.EventPublisher;
import com.shdatalink.framework.json.utils.JsonUtil;
import com.shdatalink.framework.redis.utils.RedisUtil;
import com.shdatalink.sip.server.common.constants.RedisKeyConstants;
import com.shdatalink.sip.server.config.SipConfigProperties;
import com.shdatalink.sip.server.gb28181.StreamFactory;
import com.shdatalink.sip.server.gb28181.core.bean.constants.InviteTypeEnum;
import com.shdatalink.sip.server.gb28181.core.bean.constants.TransportTypeEnum;
import com.shdatalink.sip.server.gb28181.core.builder.GBRequest;
import com.shdatalink.sip.server.media.MediaHttpClient;
import com.shdatalink.sip.server.media.MediaService;
import com.shdatalink.sip.server.media.MediaSignService;
import com.shdatalink.sip.server.media.bean.entity.req.SnapshotReq;
import com.shdatalink.sip.server.media.bean.entity.req.StartRecordReq;
import com.shdatalink.sip.server.media.event.MediaExitedEvent;
import com.shdatalink.sip.server.media.event.MediaRegisterEvent;
import com.shdatalink.sip.server.media.hook.req.*;
import com.shdatalink.sip.server.media.hook.resp.*;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.enums.MessageTypeEnum;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import com.shdatalink.sip.server.module.device.event.DeviceOnlineEvent;
import com.shdatalink.sip.server.module.device.service.DeviceChannelService;
import com.shdatalink.sip.server.module.device.service.DeviceLogService;
import com.shdatalink.sip.server.module.device.service.DeviceService;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewPlayVO;
import com.shdatalink.sip.server.module.plan.service.VideoRecordRemoteService;
import com.shdatalink.sip.server.module.plan.service.VideoRecordService;
import com.shdatalink.sip.server.module.pushstream.convert.PushStreamConvert;
import com.shdatalink.sip.server.module.pushstream.dto.MediaViewerDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import static com.shdatalink.sip.server.gb28181.core.bean.constants.SipConstant.SNAPSHOT_TOKEN;

@Slf4j
@ApplicationScoped
public class MediaHookService {

    @Inject
    VideoRecordService videoRecordService;

    @Inject
    @RestClient
    MediaHttpClient mediaHttpClient;

    @Inject
    DeviceChannelService deviceChannelService;

    @Inject
    VideoRecordRemoteService videoRecordRemoteService;

    @Inject
    EventPublisher publisher;

    @Inject
    DeviceService deviceService;

    @Inject
    Executor executor;

    @Inject
    SipConfigProperties sipConfigProperties;

    @Inject
    RedisUtil redisUtil;

    @Inject
    DeviceLogService deviceLogService;
    @Inject
    MediaService mediaService;
    @Inject
    MediaSignService mediaSignService;
    @Inject
    PushStreamConvert pushStreamConvert;


    public HookResp flowReport(FlowReportReq flowReportReq) {
        log.info("流上报事件" + flowReportReq);
        return new HookResp();
    }

    public HttpAccessResp httpAccess(HttpAccessReq httpAccessReq) {
        log.info("http访问事件" + httpAccessReq);
        return new HttpAccessResp();
    }

    public PublishResp publish(PublishReq publishReq) {
        log.info("发布流事件");
        String stream = publishReq.getStream();
        String prefix = stream.substring(0, 2);
        if(Objects.equals(prefix,InviteTypeEnum.Rtmp.getPrefix())){
            DeviceChannel channel = deviceChannelService.getById(StreamFactory.extractChannel(stream));
            if(channel == null) {
                PublishResp publishResp = new PublishResp();
                publishResp.setCode(404);
                publishResp.setMsg("通道不存在");
                return publishResp;
            }
            Device device = deviceService.getByDeviceId(channel.getDeviceId()).orElse(null);
            if(device == null) {
                PublishResp publishResp = new PublishResp();
                publishResp.setCode(404);
                publishResp.setMsg("设备不在线");
                return publishResp;
            }
            if(device.getProtocolType() != ProtocolTypeEnum.RTMP){
                PublishResp publishResp = new PublishResp();
                publishResp.setCode(403);
                publishResp.setMsg("设备不支持rtmp协议");
                return publishResp;
            }
            boolean online = true;
            device.setOnline(online);
            device.setRegisterTime(LocalDateTime.now());
            deviceService.updateById(device);
            channel.setOnline(online);
            channel.setRegisterTime(LocalDateTime.now());
            deviceChannelService.updateById(channel);
            publisher.fireAsync(new DeviceOnlineEvent(device.getDeviceId(), online));
            executor.execute(()->{
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                SnapshotReq req = new SnapshotReq();
                DevicePreviewPlayVO playUrl = mediaService.getPlayUrl(ProtocolTypeEnum.RTMP, channel.getDeviceId(), channel.getChannelId(), channel.getId());
                req.setUrl(playUrl.getRtspUrl());
                req.setTimeoutSec(30);
                req.setExpireSec(60);
                byte[] snap = mediaHttpClient.getSnap(req);
                String snapPath = sipConfigProperties.media().snapPath();
                if (!Files.exists(Paths.get(snapPath))) {
                    try {
                        Files.createDirectories(Paths.get(snapPath));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    Files.write(Paths.get(snapPath, channel.getDeviceId() + "_" + channel.getChannelId() + ".jpg"), snap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        PublishResp publishResp = new PublishResp();
        publishResp.setCode(0);
        return publishResp;
    }

    public HookResp recordMp4(RecordMp4Req recordMp4Req) {
        log.info("录制mp4事件" + recordMp4Req);
        InviteTypeEnum action = InviteTypeEnum.getByPrefix(recordMp4Req.getStream().substring(0, 2));
        if (action == InviteTypeEnum.Play || action == InviteTypeEnum.PullStream || action == InviteTypeEnum.Rtmp) {
            try {
                videoRecordService.save(recordMp4Req);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (action == InviteTypeEnum.Download) {
            String url = String.format("%s%s/%s", recordMp4Req.getFolder(), LocalDate.now().format(DateTimeFormatter.ISO_DATE), recordMp4Req.getFileName());
            videoRecordRemoteService.downloadDone(recordMp4Req.getStream(), url);
        } else {
            // 删除掉录制的视频来清理硬盘
            Path path = Paths.get("%s/%s/%s", recordMp4Req.getFolder(), LocalDate.now().format(DateTimeFormatter.ISO_DATE), recordMp4Req.getFileName());
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new HookResp(0);
    }


    public RtspRealmResp rtspRealm(RtspRealmReq rtspRealmReq) {
        log.info("rtsp认证事件");
        return new RtspRealmResp();
    }

    public RtspAuthResp rtspAuth(RtspAuthReq rtspAuthReq) {
        log.info("rtsp认证事件");
        return new RtspAuthResp();
    }

    public HookResp shellLogin(ShellLoginReq shellLoginReq) {
        log.info("shell登录事件");
        return new HookResp();
    }

    public HookResp streamChanged(StreamChangedReq streamChangedReq) {
        log.info("streamChanged: {}", JsonUtil.toJsonString(streamChangedReq));
        InviteTypeEnum action = InviteTypeEnum.getByPrefix(streamChangedReq.getStream().substring(0, 2));
        if (streamChangedReq.getRegist()) {
            if (action == InviteTypeEnum.Download) {
                Path path = null;
                try {
                    path = Files.createTempDirectory("record_download");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                // 开始录制
                StartRecordReq startRecord = new StartRecordReq();
                startRecord.setType(1);
                startRecord.setStream(streamChangedReq.getStream());
                startRecord.setCustomizedPath(path.toAbsolutePath().toString());
                startRecord.setMaxSecond(Integer.MAX_VALUE);
                mediaHttpClient.startRecord(startRecord);

                System.out.println(path.toAbsolutePath());
            } else if (action == InviteTypeEnum.Play) {
                DeviceChannel channel = deviceChannelService.getOptById(StreamFactory.extractChannel(streamChangedReq.getStream())).orElseThrow(() -> new BizException("通道不存在"));
                deviceLogService.addLog(channel.getDeviceId(), channel.getChannelId(), channel.getOnline(), MessageTypeEnum.Stream, "开始推流");
            }
        } else {
            log.info("流注销事件");
            if (action == InviteTypeEnum.Play) {
                DeviceChannel channel = deviceChannelService.getOptById(StreamFactory.extractChannel(streamChangedReq.getStream())).orElseThrow(() -> new BizException("通道不存在"));
                deviceLogService.addLog(channel.getDeviceId(), channel.getChannelId(), channel.getOnline(), MessageTypeEnum.Stream, "停止推流");
            }
        }
        if(action == InviteTypeEnum.Rtmp) {
            Integer channelId = StreamFactory.extractChannel(streamChangedReq.getStream());
            DeviceChannel deviceChannel = deviceChannelService.getById(channelId);
            if(deviceChannel != null){
                deviceService.getByDeviceId(deviceChannel.getDeviceId()).ifPresent(device -> {
                    device.setOnline(streamChangedReq.getRegist());
                    deviceService.updateById(device);
                });
                deviceChannel.setOnline(streamChangedReq.getRegist());
                deviceChannelService.updateById(deviceChannel);
                publisher.fireAsync(new DeviceOnlineEvent(deviceChannel.getDeviceId(), streamChangedReq.getRegist()));
            }
        }
        HookResp hookResp = new HookResp();
        hookResp.setCode(0);
        return hookResp;
    }

    public StreamNoneReaderResp streamNoneReader(StreamNoneReaderReq streamNoneReaderReq) {
        log.info("流无读取者事件, {}", JsonUtil.toJsonString(streamNoneReaderReq));
        String stream = streamNoneReaderReq.getStream();
        Optional<DeviceChannel> deviceChannelOptional = deviceChannelService.getOptById(StreamFactory.extractChannel(stream));
        StreamNoneReaderResp resp = new StreamNoneReaderResp();
        if (deviceChannelOptional.isEmpty()) {
            resp.setClose(true);
            return resp;
        }
        DeviceChannel channel = deviceChannelOptional.get();

        Optional<Device> deviceOptional = deviceService.getByDeviceId(channel.getDeviceId());
        if (deviceOptional.isEmpty()) {
            resp.setClose(true);
            return resp;
        }
        Device device = deviceOptional.get();
        InviteTypeEnum action = InviteTypeEnum.getByPrefix(streamNoneReaderReq.getStream().substring(0, 2));
        if (action == InviteTypeEnum.Play) {
            if (!channel.getRecording()) {
                GBRequest.bye(device.toGbDevice(channel.getChannelId())).withStreamId(streamNoneReaderReq.getStream()).execute();
                resp.setClose(true);
            }
        } else if (action == InviteTypeEnum.Playback) {
            GBRequest.bye(device.toGbDevice(channel.getChannelId())).withStreamId(streamNoneReaderReq.getStream()).execute();
            resp.setClose(true);
        } else if (action == InviteTypeEnum.Download) {
        } else {
            resp.setClose(true);
        }

        resp.setCode(0);
        return resp;
    }

    public HookResp streamNotFound(StreamNotFoundReq streamNotFoundReq) {
        log.info("流不存在事件" + streamNotFoundReq);
        HookResp hookResp = new HookResp();
        String stream = streamNotFoundReq.getStream();
        if (mediaService.mediaExists(stream)) {
            hookResp.setCode(0);
            return hookResp;
        }

        InviteTypeEnum action = InviteTypeEnum.getByPrefix(stream.substring(0,2));
        if (action == null) {
            hookResp.setCode(404);
            return hookResp;
        }

        String[] split1 = streamNotFoundReq.getParams().split("&");
        Optional<String> startOpt = Arrays.stream(split1).filter(item -> item.startsWith("start=")).findFirst();
        Optional<String> endOpt = Arrays.stream(split1).filter(item -> item.startsWith("end=")).findFirst();
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (startOpt.isPresent()) {
            startTime = LocalDateTime.parse(URLDecoder.decode(startOpt.get().replace("start=", ""), StandardCharsets.UTF_8), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        if (endOpt.isPresent()) {
            endTime = LocalDateTime.parse(URLDecoder.decode(endOpt.get().replace("end=", ""), StandardCharsets.UTF_8), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        switch (action) {
            case Play -> deviceChannelService.play(stream);
            case Playback -> {
                if (endTime == null || startTime == null) {
                    hookResp.setCode(404);
                    return hookResp;
                }
                deviceChannelService.playBack(stream, startTime, endTime);
            }
            case PullStream -> {
                DeviceChannel channel = deviceChannelService.getById(StreamFactory.extractChannel(stream));
                Device device = deviceService.getByDeviceId(channel.getDeviceId()).orElseThrow(() -> new BizException("设备不存在"));
                mediaService.addPullStream(device.getStreamUrl(), stream, TransportTypeEnum.TCP, device.getEnableAudio());
            }
            default -> {
                hookResp.setCode(404);
                return hookResp;
            }
        }


        hookResp.setCode(0);
        return hookResp;
    }

    public HookResp serverStarted(ServerStartedReqResult serverStartedReq) {
        log.info("服务器启动事件");
        publisher.fireAsync(new MediaRegisterEvent());
        HookResp hookResp = new HookResp();
        hookResp.setCode(0);
        hookResp.setMsg("服务器启动成功");
        return hookResp;
    }

    public HookResp onServerKeepalive(ServerKeepaliveReq serverKeepaliveReq) {
        HookResp hookResp = new HookResp();
        hookResp.setCode(0);
        return hookResp;
    }

    public HookResp onRtpServerTimeout(RtpServerTimeoutReq rtpServerTimeoutReq) {
        log.info("调用openRtpServer 接口，rtp server 长时间未收到数据" + rtpServerTimeoutReq);
        HookResp hookResp = new HookResp();
        hookResp.setCode(0);
        return hookResp;
    }

    public HookResp play(PlayReq playReq) {
        log.info("media hook on play: {}", JsonUtil.toJsonString(playReq));
        String params = playReq.getParams();
        HookResp hookResp = new HookResp();
        Optional<String> first = Stream.of(params.split("&")).filter(item -> item.startsWith("token="))
                .findFirst();
        if (first.isEmpty()) {
            hookResp.setCode(401);
            return hookResp;
        }
        String token = first.get().replace("token=", "");
        if (!SNAPSHOT_TOKEN.equals(token)) {
            boolean verify = mediaSignService.verify(playReq.getStream(), token);
            if (!verify) {
                hookResp.setCode(401);
                return hookResp;
            }
        }

        InviteTypeEnum action = InviteTypeEnum.getByPrefix(playReq.getStream().substring(0,2));
        if (action == null) {
            hookResp.setCode(401);
            return hookResp;
        }
        if (action == InviteTypeEnum.Playback) {
        }else{
            String viewerStr = redisUtil.get(RedisKeyConstants.PUSH_STREAM_VIEWER + playReq.getStream());
            List<MediaViewerDTO> playReqs;
            if (StringUtils.isNotBlank(viewerStr)) {
                playReqs = JsonUtil.parseObject(viewerStr, new TypeReference<>() {
                });
                if (playReqs == null) {
                    playReqs = new ArrayList<>();
                }
            } else {
                playReqs = new ArrayList<>();
            }
            MediaViewerDTO mediaViewerDTO = pushStreamConvert.convert(playReq);
            mediaViewerDTO.setPlayTime(LocalDateTime.now());
            playReqs.add(mediaViewerDTO);
            redisUtil.set(RedisKeyConstants.PUSH_STREAM_VIEWER + playReq.getStream(), JsonUtil.toJsonString(playReqs));
        }
        hookResp.setCode(0);
        return hookResp;
    }

    public HookResp onServerExited(ServerExitedReqResult req) {
        publisher.fireAsync(new MediaExitedEvent());
        HookResp hookResp = new HookResp();
        hookResp.setCode(0);
        return hookResp;
    }
}
