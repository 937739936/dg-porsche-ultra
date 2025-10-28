package com.shdatalink.sip.server.media;

import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.sip.server.config.SipConfigProperties;
import com.shdatalink.sip.server.gb28181.core.bean.constants.TransportTypeEnum;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.GbDevice;
import com.shdatalink.sip.server.gb28181.core.builder.GBRequest;
import com.shdatalink.sip.server.media.bean.entity.req.*;
import com.shdatalink.sip.server.media.bean.entity.resp.*;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewPlayVO;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDateTime;
import java.util.*;

import static com.shdatalink.sip.server.common.constants.CommonConstants.PLAY_DEFAULT_EXPIRE;

@ApplicationScoped
@Slf4j
public class MediaService {
    @Inject
    @RestClient
    MediaHttpClient mediaHttpClient;
    @Inject
    SipConfigProperties sipConfigProperties;
    @Inject
    Instance<MediaUrl> mediaUrlInstances;

    private final Map<ProtocolTypeEnum, MediaUrl> mediaUrls = new HashMap<>();

    @PostConstruct
    public void init() {
        for (MediaUrl mediaUrlInstance : mediaUrlInstances) {
            mediaUrls.put(mediaUrlInstance.type(), mediaUrlInstance);
        }
    }

    public List<MediaListResult> listMedia() {
        return listMedia(null);
    }

    public ServerNodeConfigResult getFirstServerNodeConfig() {
        List<ServerNodeConfigResult> nodes = listServerNode();
        if (nodes == null || nodes.isEmpty()) {
            return null;
        }
        return nodes.getFirst();
    }

    public List<ServerNodeConfigResult> listServerNode() {
        MediaServerResponse<List<ServerNodeConfigResult>> serverConfig = mediaHttpClient.getServerConfig();
        if (serverConfig.getCode() != 0) {
            throw new BizException("查询媒体服务失败, code=" + serverConfig.getCode());
        }
        return serverConfig.getData() != null ? serverConfig.getData() : new ArrayList<>();
    }

    public List<MediaListResult> listMedia(String stream) {
        MediaServerResponse<List<MediaListResult>> mediaList = mediaHttpClient.getMediaList(new MediaReq(stream));
        if (mediaList.getCode() != 0) {
            throw new BizException("查询媒体列表失败, code=" + mediaList.getCode());
        }
        return mediaList.getData() == null ? new ArrayList<>() : mediaList.getData();
    }

    public boolean mediaExists(String stream) {
        return listMedia(stream)
                .stream()
                .anyMatch(item -> {
                    if (item == null) {
                        return false;
                    }
                    return Objects.equals(stream, item.getStream());
                });
    }

    public boolean streamReaderExists(String stream) {
        MediaServerResponse<List<MediaListResult>> mediaList = mediaHttpClient.getMediaList(new MediaReq(stream));
        if (mediaList.getCode() != 0) {
            throw new BizException("查询媒体列表失败, code=" + mediaList.getCode());
        }
        return mediaList.getData().stream().anyMatch(item -> item.getReaderCount() > 0);
    }

    public boolean rtpServerExists(String streamId) {
        MediaServerResponse<List<ListRtpServerResult>> rtpServers = mediaHttpClient.listRtpServer();
        if (rtpServers.getCode() != 0) {
            throw new BizException("查询rtp服务列表失败, code=" + rtpServers.getCode());
        }
        return rtpServers.getData() != null && rtpServers.getData().stream().anyMatch(s -> s.getStreamId().equals(streamId));
    }

    public int openRtpServer(String stream) {
        OpenRtpServerReq req = new OpenRtpServerReq();
        req.setPort(0);
        req.setTcpMode(1);
        req.setStreamId(stream);
        OpenRtpServerResult openRtpServerResult = mediaHttpClient.openRtpServer(req);
        if (openRtpServerResult.getCode() != 0) {
            throw new BizException("rtp服务开启失败，msg=" + openRtpServerResult.getMsg());
        }
        return openRtpServerResult.getPort();
    }


    public void play(GbDevice gbDevice, String streamId) {
        if (!rtpServerExists(streamId)) {
            int port = openRtpServer(streamId);
            GBRequest.invite(gbDevice)
                    .withStreamId(streamId)
                    .withMediaAddress(sipConfigProperties.server().wanIp(), port)
                    .play()
                    .execute();
        }
    }

    public void closeStreams(String streamId, int force) {
        mediaHttpClient.closeStreams(new CloseStreamsReq(streamId, force));
    }

    public void closeStreams(String streamId) {
        mediaHttpClient.closeStreams(new CloseStreamsReq(streamId));
    }

    public void playback(GbDevice gbDevice, String streamId, LocalDateTime startTime, LocalDateTime endTime) {
        if (!rtpServerExists(streamId)) {
            int port = openRtpServer(streamId);
            GBRequest.invite(gbDevice)
                    .withStreamId(streamId)
                    .withMediaAddress(sipConfigProperties.server().wanIp(), port)
                    .playBack(startTime, endTime)
                    .execute();
        }
    }

    public void download(GbDevice gbDevice, String stream, LocalDateTime startTime, LocalDateTime endTime) {
        int port = openRtpServer(stream);
        GBRequest.invite(gbDevice)
                .withStreamId(stream)
                .withMediaAddress(sipConfigProperties.server().wanIp(), port)
                .download(startTime, endTime)
                .execute();
    }

    public List<TcpSessionResult> getAllSessions() {
        MediaServerResponse<List<TcpSessionResult>> allSession = mediaHttpClient.getAllSession(new TcpSessionReq());
        if (allSession.getCode() != 0) {
            throw new BizException("查询媒体列表失败, code=" + allSession.getCode());
        }
        return allSession.getData();
    }


    public DevicePreviewPlayVO getPlayUrl(Device device, DeviceChannel channel) {
        return getPlayUrl(device.getProtocolType(), channel.getDeviceId(), channel.getChannelId(), channel.getId());
    }
    public DevicePreviewPlayVO getPlayUrl(Device device, DeviceChannel channel, int expire) {
        return getPlayUrl(device.getProtocolType(), channel.getDeviceId(), channel.getChannelId(), channel.getId(), expire);
    }

    public DevicePreviewPlayVO getPlayUrl(ProtocolTypeEnum type, String deviceId, String channelId, Integer channelPrimaryId) {
        return getPlayUrl(type, deviceId, channelId, channelPrimaryId, PLAY_DEFAULT_EXPIRE);
    }
    public DevicePreviewPlayVO getPlayUrl(ProtocolTypeEnum type, String deviceId, String channelId, Integer channelPrimaryId, int expire) {
        DevicePreviewPlayVO play = mediaUrls.get(type).play(channelPrimaryId, expire);
        play.setDeviceId(deviceId);
        play.setChannelId(channelId);
        return play;
    }

    public String rtmpUrl(String stream) {
        ServerNodeConfigResult serverConfig = getFirstServerNodeConfig();
        String mediaIp = sipConfigProperties.server().wanIp();
        return String.format("rtmp://%s:%d/rtp/%s", mediaIp, serverConfig.getRtmpPort(), stream);
    }

    public DevicePreviewPlayVO getPlaybackUrl(Device device, DeviceChannel channel, LocalDateTime start) {
        DevicePreviewPlayVO playback = mediaUrls.get(device.getProtocolType()).playback(channel.getId(), start);
        playback.setDeviceId(device.getDeviceId());
        playback.setChannelId(channel.getChannelId());
        return playback;
    }

    public Boolean addPullStream(String url, String streamId, TransportTypeEnum transportType, Boolean enableAudio) {
        AddStreamProxyReq streamProxyItem = MediaReq.getRtpInstance(streamId, AddStreamProxyReq.class);
        streamProxyItem.setUrl(url);
        streamProxyItem.setStream(streamId);
        streamProxyItem.setRtpType(transportType == TransportTypeEnum.TCP ? 0 : 1);
        streamProxyItem.setAutoClose(true);
        streamProxyItem.setEnableAudio(enableAudio);
        streamProxyItem.setAddMuteAudio(!enableAudio);
        MediaServerResponse<AddStreamProxyResult> response = mediaHttpClient.addStreamProxy(streamProxyItem);
        return response.getCode() == 0;
    }

    public String getSnapshotUrl(Device device, Integer channelPrimaryId) {
        if (device.getProtocolType() == ProtocolTypeEnum.PULL) {
            return device.getStreamUrl();
        }
        return getSnapshotUrl(device.getProtocolType(), channelPrimaryId);
    }

    public String getSnapshotUrl(ProtocolTypeEnum type, Integer channelPrimaryId) {
        return mediaUrls.get(type).snapshot(channelPrimaryId);
    }

//    public void playPullStream(String streamId) {
//        DeviceChannel channel = deviceChannelService.getById(StreamFactory.extractChannel(streamId));
//        if (channel == null) {
//            throw new BizException("通道不存在");
//        }
//        Device device = deviceMapper.selectByDeviceId(channel.getDeviceId());
//        addPullStream(device.getStreamUrl(), streamId, TransportTypeEnum.TCP, device.getEnableAudio());
//    }
}
