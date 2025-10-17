package com.shdatalink.sip.server.media;

import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.sip.server.config.SipConfigProperties;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.GbDevice;
import com.shdatalink.sip.server.gb28181.core.builder.GBRequest;
import com.shdatalink.sip.server.media.bean.entity.req.CloseStreamsReq;
import com.shdatalink.sip.server.media.bean.entity.req.MediaReq;
import com.shdatalink.sip.server.media.bean.entity.req.OpenRtpServerReq;
import com.shdatalink.sip.server.media.bean.entity.req.TcpSessionReq;
import com.shdatalink.sip.server.media.bean.entity.resp.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class MediaService {
    @Inject
    @RestClient
    MediaHttpClient mediaHttpClient;
    @Inject
    SipConfigProperties sipConfigProperties;

    public List<MediaListResult> listMedia() {
        MediaServerResponse<List<MediaListResult>> mediaList = mediaHttpClient.getMediaList(new MediaReq());
        if (mediaList.getCode() != null) {
            throw new BizException("查询媒体列表失败, code=" + mediaList.getCode());
        }
        return mediaList.getData();
    }

    public boolean mediaExists(String stream) {
        MediaServerResponse<List<MediaListResult>> mediaList = mediaHttpClient.getMediaList(new MediaReq(stream));
        if (mediaList.getCode() != null) {
            throw new BizException("查询媒体列表失败, code=" + mediaList.getCode());
        }
        return mediaList.getData()
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
        if (mediaList.getCode() != null) {
            throw new BizException("查询媒体列表失败, code=" + mediaList.getCode());
        }
        return mediaList.getData().stream().anyMatch(item -> item.getReaderCount() > 0);
    }

    public boolean rtpServerExists(String streamId) {
        MediaServerResponse<List<ListRtpServerResult>> rtpServers = mediaHttpClient.listRtpServer();
        if (rtpServers.getCode() != null) {
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

    public List<TcpSessionResult> getAllSessions() {
        MediaServerResponse<List<TcpSessionResult>> allSession = mediaHttpClient.getAllSession(new TcpSessionReq());
        if (allSession.getCode() != null) {
            throw new BizException("查询媒体列表失败, code=" + allSession.getCode());
        }
        return allSession.getData();
    }
}
