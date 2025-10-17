package com.shdatalink.sip.server.module.pushstream.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.json.utils.JsonUtil;
import com.shdatalink.redis.utils.RedisUtil;
import com.shdatalink.sip.server.common.constants.RedisKeyConstants;
import com.shdatalink.sip.server.config.SipConfigProperties;
import com.shdatalink.sip.server.gb28181.StreamFactory;
import com.shdatalink.sip.server.gb28181.core.bean.constants.InviteTypeEnum;
import com.shdatalink.sip.server.gb28181.core.builder.GBRequest;
import com.shdatalink.sip.server.media.MediaHttpClient;
import com.shdatalink.sip.server.media.MediaService;
import com.shdatalink.sip.server.media.bean.entity.Track;
import com.shdatalink.sip.server.media.bean.entity.req.CloseStreamsReq;
import com.shdatalink.sip.server.media.bean.entity.resp.MediaListResult;
import com.shdatalink.sip.server.media.bean.entity.resp.MediaPlayerResult;
import com.shdatalink.sip.server.media.bean.entity.resp.TcpSessionResult;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import com.shdatalink.sip.server.module.device.mapper.DeviceChannelMapper;
import com.shdatalink.sip.server.module.device.mapper.DeviceMapper;
import com.shdatalink.sip.server.module.pushstream.dto.ChannelBaseInfoDTO;
import com.shdatalink.sip.server.module.pushstream.dto.MediaViewerDTO;
import com.shdatalink.sip.server.module.pushstream.enums.CodecTypeEnum;
import com.shdatalink.sip.server.module.pushstream.vo.PushStreamPageResp;
import com.shdatalink.sip.server.module.pushstream.vo.PushStreamResp;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class PushStreamService {

    @Inject
    DeviceChannelMapper deviceChannelMapper;
    @Inject
    @RestClient
    MediaHttpClient mediaHttpClient;
    @Inject
    MediaService mediaService;
    @Inject
    SipConfigProperties sipConfigProperties;
    @Inject
    RedisUtil redisUtil;
    @Inject
    DeviceMapper deviceMapper;

    public IPage<PushStreamPageResp> page(Integer page, Integer pageSize) {
        List<MediaListResult> mediaListResults = mediaService.listMedia();
        if (CollectionUtils.isEmpty(mediaListResults)) {
            return new Page<>(page, pageSize);
        }
        Map<String, MediaListResult> mediaDataMap = mediaListResults.stream().collect(Collectors.toMap(MediaListResult::getStream, item -> item, (v1, v2) -> v1));
        List<MediaListResult> values = new ArrayList<>(mediaDataMap.values());
        List<MediaListResult> mediaListResultList = subList(values, page, pageSize);
        List<PushStreamPageResp> pushStreamList = new ArrayList<>();
        for (MediaListResult mediaListResult : mediaListResultList) {
            PushStreamPageResp resp = new PushStreamPageResp();
            String streamId = mediaListResult.getStream();
            Integer channelId = StreamFactory.extractChannel(streamId);
            ChannelBaseInfoDTO onlinePushStream = deviceChannelMapper.getBaseChannelInfo(channelId);
            if (onlinePushStream == null) {
                continue;
            }
            InviteTypeEnum action = InviteTypeEnum.getByPrefix(streamId.substring(0, 2));
            if (action == InviteTypeEnum.Playback) {
                resp.setStreamType("回放流");
            } else if (action == InviteTypeEnum.Download) {
                resp.setStreamType("下载流");
            } else {
                resp.setStreamType("直播流");
            }
            resp.setDeviceId(onlinePushStream.getDeviceId());
            resp.setChannelId(onlinePushStream.getChannelId());
            resp.setStreamId(streamId);
            String snapPath = sipConfigProperties.media().snapPath();
            Path path = Paths.get(snapPath, onlinePushStream.getDeviceId() + "_" + onlinePushStream.getChannelId() + ".jpg");
            if (Files.exists(path)) {
                try {
                    byte[] bytes = Files.readAllBytes(path);
                    resp.setBase64(Base64.getEncoder().encodeToString(bytes));
                } catch (IOException e) {
                    log.error("读取快照文件异常", e);
                }
            }
            if (onlinePushStream.getProtocolType() == ProtocolTypeEnum.RTMP) {
                resp.setStreamUrl(mediaService.rtmpUrl(streamId));
            } else if (onlinePushStream.getProtocolType() == ProtocolTypeEnum.PULL) {
                resp.setStreamUrl(onlinePushStream.getStreamUrl());
            } else if (onlinePushStream.getProtocolType() == ProtocolTypeEnum.GB28181) {
                MediaPlayerResult originSock = mediaListResult.getOriginSock();
                if (originSock != null) {
                    resp.setStreamUrl(originSock.getPeerIp() + ":" + originSock.getPeerPort());
                }
            }
            resp.setOnline(onlinePushStream.getOnline());

            resp.setStreamId(streamId);
            resp.setBytesSpeed(mediaListResult.getBytesSpeed());
            resp.setAliveSecond(mediaListResult.getAliveSecond());
            //当无人观看时，保存当前流缓存，有人观看时，删除当前流缓存
            String redisKey = RedisKeyConstants.NO_VIEWER_PUSH_STREAM + streamId;
            if (mediaListResult.getTotalReaderCount() == 0) {
                String s = redisUtil.get(redisKey);
                if (StringUtils.isBlank(s)) {
                    redisUtil.set(redisKey, System.currentTimeMillis() + "");
                }
            } else {
                redisUtil.delete(redisKey);
            }
            Object o = redisUtil.get(redisKey);
            if (o != null) {
                long l = (System.currentTimeMillis() - Long.parseLong(o.toString())) / 1000;
                if (l > resp.getAliveSecond()) {
                    l = resp.getAliveSecond();
                }
                resp.setNoViewerSecond(l);
            }
            resp.setTotalReaderCount(mediaListResult.getTotalReaderCount());
            Instant instant = Instant.ofEpochSecond(mediaListResult.getCreateStamp());
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            resp.setCreateTime(localDateTime);
            for (Track track : mediaListResult.getTracks()) {
                if (track.getCodecType() == 1) {
                    PushStreamPageResp.AudioInfo audioInfo = new PushStreamPageResp.AudioInfo();
                    audioInfo.setSampleRate(track.getSampleRate());
                    audioInfo.setCodecName(CodecTypeEnum.parseText(track.getCodecId()));
                    resp.setAudio(audioInfo);
                } else if (track.getCodecType() == 0) {
                    PushStreamPageResp.VideoInfo videoInfo = new PushStreamPageResp.VideoInfo();
                    videoInfo.setFps(track.getFps());
                    videoInfo.setHeight(track.getHeight());
                    videoInfo.setWidth(track.getWidth());
                    videoInfo.setCodecName(CodecTypeEnum.parseText(track.getCodecId()));
                    resp.setVideo(videoInfo);
                }
            }
            pushStreamList.add(resp);
        }
        Page<PushStreamPageResp> respPage = new Page<>(page, pageSize);
        respPage.setRecords(pushStreamList);
        return respPage;
    }

    public List<PushStreamResp> detail(String streamId) {
        String viewerStr = redisUtil.get(RedisKeyConstants.PUSH_STREAM_VIEWER + streamId);
        if (StringUtils.isBlank(viewerStr)) {
            return List.of();
        }
        List<MediaViewerDTO> playReqs = JsonUtil.parseObject(viewerStr, new TypeReference<>() {
        });
        List<TcpSessionResult> allSession = mediaService.getAllSessions();
        return playReqs.stream().filter(item -> allSession.stream().anyMatch(session -> session.getId().equals(item.getId()))).map(item -> {
            PushStreamResp resp = new PushStreamResp();
            resp.setId(item.getId());
            resp.setRemoteAddress(item.getIp() + ":" + item.getPort());
            item.setProtocol(item.getProtocol());
            resp.setProtocol(item.getProtocol());
            resp.setCreateTime(item.getPlayTime());
            resp.setLastModifiedTime(LocalDateTime.now());
            return resp;
        }).toList();
    }

    public boolean closeStream(String streamId) {
        Integer channelId = StreamFactory.extractChannel(streamId);
        DeviceChannel deviceChannel = deviceChannelMapper.selectById(channelId);
        if (deviceChannel == null) {
            throw new BizException("通道不存在");
        }
        if (deviceChannel.getRecording() != null && deviceChannel.getRecording()) {
            throw new BizException("通道正在录像，不能关闭推流");
        }
        Device device = deviceMapper.selectByChannelId(deviceChannel.getChannelId());
        if (device.getProtocolType() == ProtocolTypeEnum.GB28181) {
            GBRequest.bye(device.toGbDevice(deviceChannel.getChannelId())).withStreamId(streamId).execute();
        }
        mediaHttpClient.closeStreams(new CloseStreamsReq(streamId, 1));
        return true;
    }

    public static List<MediaListResult> subList(List<MediaListResult> list, Integer pageNo, Integer pageSize) {
        int fromIndex = (pageNo - 1) * pageSize;
        if (fromIndex >= list.size()) {
            return Collections.emptyList();
        }
        int toIndex = Math.min(fromIndex + pageSize, list.size());
        return list.subList(fromIndex, toIndex);
    }

}
