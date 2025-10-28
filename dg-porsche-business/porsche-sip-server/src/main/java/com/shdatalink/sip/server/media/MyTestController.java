package com.shdatalink.sip.server.media;

import com.shdatalink.framework.common.annotation.Anonymous;
import com.shdatalink.sip.server.media.bean.entity.DeleteRecordDirectory;
import com.shdatalink.sip.server.media.bean.entity.req.*;
import com.shdatalink.sip.server.media.bean.entity.resp.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


@Path("/media")
@Anonymous
public class MyTestController {


    @Inject
    @RestClient
    MediaHttpClient zlmRestTemplate;


    /**
     * http :9000/media/getApiList
     */
    @Path("/getApiList")
    @GET
    public MediaServerResponse<List<String>> getApiList() {
        return zlmRestTemplate.getApiList();
    }

    /**
     * http :9000/media/getThreadsLoad
     */
    @GET
    @Path("/getThreadsLoad")
    public MediaServerResponse<List<ThreadLoadResult>> getThreadsLoad() {
        return zlmRestTemplate.getThreadsLoad();
    }

    /**
     * http :9000/media/getWorkThreadsLoad
     */
    @Path("/getWorkThreadsLoad")
    @GET
    public MediaServerResponse<List<ThreadLoadResult>> workThreadsLoad() {
        return zlmRestTemplate.getWorkThreadsLoad();
    }

    /**
     * http :9000/media/getServerConfig
     */
    @Path("/getServerConfig")
    @GET
    public MediaServerResponse<List<ServerNodeConfigResult>> getServerConfig() {
        return zlmRestTemplate.getServerConfig();
    }

    /**
     * http :9000/media/getStatistic
     */
    @Path("/getStatistic")
    @GET
    public MediaServerResponse<MediaStatisticsResult> statistic() {
        return zlmRestTemplate.getStatistic();
    }

    /**
     * http :9000/media/restartServer
     */
    @Path("/restartServer")
    @GET
    public MediaServerResponse<Void> restartServer() {
        return zlmRestTemplate.restartServer();
    }

    /**
     * http :9000/media/getMediaList
     */
    @Path("/getMediaList")
    @GET
    public MediaServerResponse<List<MediaListResult>> getMediaList(@QueryParam("stream") String stream) {
        return zlmRestTemplate.getMediaList(new MediaReq(stream));
    }

    /**
     * http :9000/media/getVersion
     */
    @Path("/getVersion")
    @GET
    public MediaServerResponse<MediaVersion> getVersion() {
        return zlmRestTemplate.getVersion();
    }

    /**
     * http :9000/media/closeStreams vhost="__defaultVhost__"
     */
    @Path("/closeStreams")
    @POST
    public MediaServerResponse<CloseStreamsResult> closeStreams(CloseStreamsReq closeStreamsReq) {
        return zlmRestTemplate.closeStreams(closeStreamsReq);
    }

    /**
     * http :9000/media/getAllSession
     */
    @Path("/getAllSession")
    @GET
    public MediaServerResponse<List<TcpSessionResult>> getAllSession() {
        return zlmRestTemplate.getAllSession(new TcpSessionReq());
    }

    /**
     * http :9000/media/kickSession  id="47-83"
     */
    @Path("/kickSession")
    @POST
    public void kickSession(KillSessionReq killSessionReq) {
        zlmRestTemplate.kickSession(killSessionReq);
    }

    /**
     * http :9000/media/kickSessions  local_port=9092
     */
    @Path("/kickSessions")
    @POST
    public void kickSessions(KillSessionsReq killSessionReq) {
        zlmRestTemplate.kickSessions(killSessionReq);
    }

    /**
     * http :9000/media/addStreamProxy  vhost="__defaultVhost__"  app="live" stream="test" url="rtsp://admin:shdatalink89@192.168.20.180:554/Streaming/Channels/101"
     * rtsp://192.168.20.215:554/live/test?token=abcdefg&field=value
     */
    @Path("/addStreamProxy")
    @GET
    public MediaServerResponse<AddStreamProxyResult> addStreamProxy(@QueryParam("stream") String stream, @QueryParam("streamUrl") String streamUrl) {
        AddStreamProxyReq addStreamProxyReq = MediaReq.getRtpInstance(stream, AddStreamProxyReq.class);
        addStreamProxyReq.setUrl(streamUrl);
        return zlmRestTemplate.addStreamProxy(addStreamProxyReq);
    }

    /**
     * http :9000/media/delStreamProxy  key="__defaultVhost__/live/test"
     */
    @Path("/delStreamProxy")
    @POST
    public MediaServerResponse<DelStreamProxyResult> delStreamProxy(DelStreamProxyReq key) {
        return zlmRestTemplate.delStreamProxy(key);
    }

    /**
     * http :9000/media/addStreamPusherProxy  vhost="__defaultVhost__" app="live" stream="test" dst_url="rtmp://192.168.20.215/live/push"
     */
    @Path("/addStreamPusherProxy")
    @POST
    public MediaServerResponse<AddStreamProxyResult> addStreamPusherProxy(AddStreamPusherReq streamPusherItem) {
        return zlmRestTemplate.addStreamPusherProxy(streamPusherItem);
    }

    /**
     * http :9000/media/delStreamPusherProxy  key="rtmp/__defaultVhost__/live/test/457656819377fa9a72c8e5c05e92732c"
     */
    @Path("/delStreamPusherProxy")
    @POST
    public MediaServerResponse<DelStreamProxyResult> delStreamPusherProxy(DelStreamProxyReq key) {
        return zlmRestTemplate.delStreamPusherProxy(key);
    }

    /**
     * http :9000/media/addFFmpegSource  src_url="rtsp://admin:shdatalink89@192.168.20.180:554/Streaming/Channels/101" dst_url="rtmp://127.0.0.1/live/stream_form_ffmpeg" timeout_ms="10000" enable_hls="false" enable_mp4="false"
     */
    @Path("/addFFmpegSource")
    @POST
    public MediaServerResponse<AddStreamProxyResult> addFFmpegSource(AddFFmpegSourceReq addFFmpegSourceReq) {
        return zlmRestTemplate.addFFmpegSource(addFFmpegSourceReq);
    }

    /**
     * http :9000/media/delFFmpegSource  key="43dc04cca7dc3e079c9a997b7ae417ff"
     */
    @Path("/delFFmpegSource")
    @POST
    public MediaServerResponse<DelStreamProxyResult> delFFmpegSource(DelStreamProxyReq key) {
        return zlmRestTemplate.delFFmpegSource(key);
    }


    /**
     * http :9000/media/getMediaPlayerList vhost="__defaultVhost__" app="live" stream="0500000063" schema="rtmp"
     */
    @Path("/getMediaPlayerList")
    @POST
    public MediaServerResponse<List<MediaPlayerResult>> getMediaPlayerList(MediaReq mediaReq) {
        return zlmRestTemplate.getMediaPlayerList(mediaReq);
    }

    /**
     * http :9000/media/broadcastMessage  vhost="__defaultVhost__" app="live" stream="test" data="{\"type\":\"hello\",\"data\":[\"world\"]}"
     */
    @Path("/broadcastMessage")
    @POST
    public MediaServerResponse broadcastMessage(BroadcastMessageReq req) {
        return zlmRestTemplate.broadcastMessage(req);
    }


    /**
     * http GET :9000/getMp4RecordFile  vhost="__defaultVhost__"  app="live" stream="test"
     * 实际按照是否有对应日期的文件夹返回
     */
    @Path("/getMp4RecordFile")
    public MediaServerResponse<GetMp4RecordFileResult> getMp4RecordFile(GetMp4FileReq getMp4FileReq) {
        return zlmRestTemplate.getMp4RecordFile(getMp4FileReq);
    }

    /**
     * http :9000/media/deleteRecordDirectory  vhost="__defaultVhost__" app="live" stream="test" period="2025-07-13"
     */
    @Path("/deleteRecordDirectory")
    @POST
    public DeleteRecordDirectory deleteRecordDirectory(DeleteRecordDictionaryReq req) {
        return zlmRestTemplate.deleteRecordDirectory(req);
    }


    /**
     * http :9000/media/startRecord type="1" vhost="__defaultVhost__"  app="rtp" stream="0100000001"
     */
    @Path("/startRecord")
    @POST
    public MediaServerResponse startRecord(StartRecordReq recordReq) {
        return zlmRestTemplate.startRecord(recordReq);
    }


    /**
     * http :9000/media/stopRecord type="1" vhost="__defaultVhost__"  app="rtp" stream="0100000001"
     */
    @Path("/stopRecord")
    @POST
    public MediaServerResponse stopRecord(StartRecordReq recordReq) {
        return zlmRestTemplate.stopRecord(recordReq);
    }

    /**
     * http :9000/media/isRecording  type="1" vhost="__defaultVhost__"  app="rtp" stream="0100000001"
     */
    @Path("/isRecording")
    @POST
    public IsRecordingResult isRecording(RecordReq recordReq) {
        return zlmRestTemplate.isRecording(recordReq);
    }


    /**
     * http :9000/media/media/getSnap  url="rtsp://admin:shdatalink89@192.168.20.180:554/Streaming/Channels/101"
     */
    @Path("/getSnap")
    @POST
    public boolean getSnap(SnapshotReq snapshotReq) throws IOException {
        byte[] snap = zlmRestTemplate.getSnap(snapshotReq);
        Files.write(Paths.get("snap.jpg"), snap);
        return true;
    }

    /**
     * http :9000/media/getRtpInfo stream_id="0100000001"
     */
    @Path("/getRtpInfo")
    @POST
    public RtpInfoResult getRtpInfo(GetRtpInfoReq streamId) {
        return zlmRestTemplate.getRtpInfo(streamId);
    }

    /**
     * http :9000/media/openRtpServer port="40009"  tcp_mode="0" stream_id="open001"
     * 10秒自动关闭
     */
    @Path("/openRtpServer")
    @POST
    public OpenRtpServerResult openRtpServer(OpenRtpServerReq openRtpServerReq) {
        return zlmRestTemplate.openRtpServer(openRtpServerReq);
    }

    /**
     * http :9000/media/openRtpServerMultiplex port="40009"  tcp_mode="1" stream_id="open001"
     * 不会自动关闭!!!!
     */
    @Path("/openRtpServerMultiplex")
    @POST
    public OpenRtpServerResult openRtpServerMultiplex(OpenRtpServerReq req) {
        return zlmRestTemplate.openRtpServerMultiplex(req);
    }


    /**
     * http :9000/media/closeRtpServer stream_id="0100000001"
     */
    @Path("/closeRtpServer")
    @POST
    public CloseRtpServerResult closeRtpServer(CloseRtpServerReq rtpServer) {
        return zlmRestTemplate.closeRtpServer(rtpServer);
    }

    /**
     * http :9000/media/listRtpServer
     */
    @Path("/listRtpServer")
    public MediaServerResponse<List<ListRtpServerResult>> listRtpServer() {
        return zlmRestTemplate.listRtpServer();
    }


    /**
     * http :9000/media/startSendRtp  vhost="__defaultVhost__" app="live" stream="test" ssrc="5001" dst_url="192.168.20.215" dst_port="50001" is_udp=true
     */
    @Path("/startSendRtp")
    @POST
    public StartSendRtpResult startSendRtp(StartSendRtpReq req) {
        return zlmRestTemplate.startSendRtp(req);
    }

    @Path("/startSendRtpPassive")
    @POST
    public StartSendRtpResult startSendRtpPassive(StartSendRtpPassiveReq req) {
        return zlmRestTemplate.startSendRtpPassive(req);
    }

    /**
     * http :9000/media/stopSendRtp  vhost="__defaultVhost__" app="live" stream="test" ssrc="5001"
     */
    @Path("/stopSendRtp")
    @POST
    public MediaServerResponse<Void> stopSendRtp(StopSendRtpReq req) {
        return zlmRestTemplate.stopSendRtp(req);
    }


    /**
     * http :9000/media/listRtpSender vhost="____defaultVhost____"  app="live" stream="test"
     */
    @Path("/listRtpSender")
    @POST
    public RtpSenderListResult listRtpSender(MediaReq req) {
        return zlmRestTemplate.listRtpSender(req);
    }

}
