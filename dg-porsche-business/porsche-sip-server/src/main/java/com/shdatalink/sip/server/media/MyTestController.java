package com.shdatalink.sip.server.media;

import com.shdatalink.sip.server.config.annotation.Anonymous;
import com.shdatalink.sip.server.media.bean.entity.DeleteRecordDirectory;
import com.shdatalink.sip.server.media.bean.entity.req.*;
import com.shdatalink.sip.server.media.bean.entity.resp.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


@RestController
@RequestMapping("/media")
@Anonymous
public class MyTestController {


    @Autowired
    private MediaHttpClient zlmRestTemplate;




    /**
     * http :9000/media/getApiList
     */
    @GetMapping("/getApiList")
    public List<String> getApiList() {
        return zlmRestTemplate.getApiList();
    }

    /**
     * http :9000/media/getThreadsLoad
     */
    @GetMapping("/getThreadsLoad")
    public List<ThreadLoadResult> getThreadsLoad() {
        return zlmRestTemplate.getThreadsLoad();
    }

    /**
     * http :9000/media/getWorkThreadsLoad
     */
    @GetMapping("/getWorkThreadsLoad")
    public List<ThreadLoadResult> workThreadsLoad() {
        return zlmRestTemplate.getWorkThreadsLoad();
    }

    /**
     * http :9000/media/getServerConfig
     */
    @GetMapping("/getServerConfig")
    public List<ServerNodeConfigResult> getServerConfig() {
        return zlmRestTemplate.getServerConfig();
    }

    /**
     * http :9000/media/getStatistic
     */
    @GetMapping("/getStatistic")
    public MediaStatisticsResult statistic() {
        return zlmRestTemplate.getStatistic();
    }

    /**
     * http :9000/media/restartServer
     */
    @GetMapping("/restartServer")
    public MediaServerResponse restartServer() {
        return zlmRestTemplate.restartServer();
    }

    /**
     * http :9000/media/getMediaList
     */
    @GetMapping("/getMediaList")
    public List<MediaListResult> getMediaList(String stream) {
        return zlmRestTemplate.getMediaList(stream);
    }

    /**
     * http :9000/media/getVersion
     */
    @GetMapping("/getVersion")
    public MediaVersion getVersion() {
        return zlmRestTemplate.getVersion();
    }

    /**
     * http :9000/media/closeStreams vhost="__defaultVhost__"
     */
    @PostMapping("/closeStreams")
    public CloseStreamsResult closeStreams(@RequestBody CloseStreamsReq closeStreamsReq) {
        return zlmRestTemplate.closeStreams(closeStreamsReq);
    }

    /**
     * http :9000/media/getAllSession
     */
    @GetMapping("/getAllSession")
    public List<TcpSessionResult> getAllSession() {
        return zlmRestTemplate.getAllSession(new TcpSessionReq());
    }

    /**
     * http :9000/media/kickSession  id="47-83"
     */
    @PostMapping("/kickSession")
    public void kickSession(@RequestBody KillSessionReq killSessionReq) {
        zlmRestTemplate.kickSession(killSessionReq);
    }

    /**
     * http :9000/media/kickSessions  local_port=9092
     */
    @PostMapping("/kickSessions")
    public void kickSessions(@RequestBody KillSessionsReq killSessionReq) {
        zlmRestTemplate.kickSessions(killSessionReq);
    }

    /**
     * http :9000/media/addStreamProxy  vhost="__defaultVhost__"  app="live" stream="test" url="rtsp://admin:shdatalink89@192.168.20.180:554/Streaming/Channels/101"
     * rtsp://192.168.20.215:554/live/test?token=abcdefg&field=value
     */
    @GetMapping("/addStreamProxy")
    public AddStreamProxyResult addStreamProxy(@RequestParam String stream, @RequestParam String streamUrl) {
        AddStreamProxyReq addStreamProxyReq = MediaReq.getRtpInstance(stream, AddStreamProxyReq.class);
        addStreamProxyReq.setUrl(streamUrl);
        return zlmRestTemplate.addStreamProxy(addStreamProxyReq);
    }

    /**
     * http :9000/media/delStreamProxy  key="__defaultVhost__/live/test"
     */
    @PostMapping("/delStreamProxy")
    public DelStreamProxyResult delStreamProxy(@RequestBody DelStreamProxyReq key) {
        return zlmRestTemplate.delStreamProxy(key);
    }

    /**
     * http :9000/media/addStreamPusherProxy  vhost="__defaultVhost__" app="live" stream="test" dst_url="rtmp://192.168.20.215/live/push"
     */
    @PostMapping("/addStreamPusherProxy")
    public AddStreamProxyResult addStreamPusherProxy(@RequestBody AddStreamPusherReq streamPusherItem) {
        return zlmRestTemplate.addStreamPusherProxy(streamPusherItem);
    }

    /**
     * http :9000/media/delStreamPusherProxy  key="rtmp/__defaultVhost__/live/test/457656819377fa9a72c8e5c05e92732c"
     */
    @PostMapping("/delStreamPusherProxy")
    public DelStreamProxyResult delStreamPusherProxy(@RequestBody DelStreamProxyReq key) {
        return zlmRestTemplate.delStreamPusherProxy(key);
    }

    /**
     * http :9000/media/addFFmpegSource  src_url="rtsp://admin:shdatalink89@192.168.20.180:554/Streaming/Channels/101" dst_url="rtmp://127.0.0.1/live/stream_form_ffmpeg" timeout_ms="10000" enable_hls="false" enable_mp4="false"
     */
    @PostMapping("/addFFmpegSource")
    public AddStreamProxyResult addFFmpegSource(@RequestBody AddFFmpegSourceReq addFFmpegSourceReq) {
        return zlmRestTemplate.addFFmpegSource(addFFmpegSourceReq);
    }

    /**
     * http :9000/media/delFFmpegSource  key="43dc04cca7dc3e079c9a997b7ae417ff"
     */
    @PostMapping("/delFFmpegSource")
    public DelStreamProxyResult delFFmpegSource(@RequestBody DelStreamProxyReq key) {
        return zlmRestTemplate.delFFmpegSource(key);
    }


    /**
     * http :9000/media/getMediaPlayerList vhost="__defaultVhost__" app="live" stream="0500000063" schema="rtmp"
     */
    @PostMapping("/getMediaPlayerList")
    public List<MediaPlayerResult> getMediaPlayerList(@RequestBody MediaReq mediaReq) {
        return zlmRestTemplate.getMediaPlayerList(mediaReq);
    }

    /**
     * http :9000/media/broadcastMessage  vhost="__defaultVhost__" app="live" stream="test" data="{\"type\":\"hello\",\"data\":[\"world\"]}"
     */
    @PostMapping("/broadcastMessage")
    public MediaServerResponse broadcastMessage(@RequestBody BroadcastMessageReq req) {
        return zlmRestTemplate.broadcastMessage(req);
    }


    /**
     * http GET :9000/getMp4RecordFile  vhost="__defaultVhost__"  app="live" stream="test"
     * 实际按照是否有对应日期的文件夹返回
     */
    @GetMapping("/getMp4RecordFile")
    public GetMp4RecordFileResult getMp4RecordFile(@RequestBody GetMp4FileReq getMp4FileReq) {
        return zlmRestTemplate.getMp4RecordFile(getMp4FileReq);
    }

    /**
     * http :9000/media/deleteRecordDirectory  vhost="__defaultVhost__" app="live" stream="test" period="2025-07-13"
     */
    @PostMapping("/deleteRecordDirectory")
    public DeleteRecordDirectory deleteRecordDirectory(@RequestBody DeleteRecordDictionaryReq req) {
        return zlmRestTemplate.deleteRecordDirectory(req);
    }


    /**
     * http :9000/media/startRecord type="1" vhost="__defaultVhost__"  app="rtp" stream="0100000001"
     */
    @PostMapping("/startRecord")
    public MediaServerResponse startRecord(@RequestBody StartRecordReq recordReq) {
        return zlmRestTemplate.startRecord(recordReq);
    }


    /**
     * http :9000/media/stopRecord type="1" vhost="__defaultVhost__"  app="rtp" stream="0100000001"
     */
    @PostMapping("/stopRecord")
    public MediaServerResponse stopRecord(@RequestBody StartRecordReq recordReq) {
        return zlmRestTemplate.stopRecord(recordReq);
    }

    /**
     * http :9000/media/isRecording  type="1" vhost="__defaultVhost__"  app="rtp" stream="0100000001"
     */
    @PostMapping("/isRecording")
    public IsRecordingResult isRecording(@RequestBody StartRecordReq recordReq) {
        return zlmRestTemplate.isRecording(recordReq);
    }


    /**
     * http :9000/media/media/getSnap  url="rtsp://admin:shdatalink89@192.168.20.180:554/Streaming/Channels/101"
     */
    @PostMapping("/getSnap")
    public boolean getSnap(@RequestBody SnapshotReq snapshotReq) throws IOException {
        byte[] snap = zlmRestTemplate.getSnap(snapshotReq);
        Files.write(Paths.get("snap.jpg"), snap);
        return true;
    }

    /**
     * http :9000/media/getRtpInfo stream_id="0100000001"
     */
    @PostMapping("/getRtpInfo")
    public RtpInfoResult getRtpInfo(@RequestBody GetRtpInfoReq streamId) {
        return zlmRestTemplate.getRtpInfo(streamId);
    }

    /**
     * http :9000/media/openRtpServer port="40009"  tcp_mode="0" stream_id="open001"
     * 10秒自动关闭
     */
    @PostMapping("/openRtpServer")
    public OpenRtpServerResult openRtpServer(@RequestBody OpenRtpServerReq openRtpServerReq) {
        return zlmRestTemplate.openRtpServer(openRtpServerReq);
    }

    /**
     * http :9000/media/openRtpServerMultiplex port="40009"  tcp_mode="1" stream_id="open001"
     * 不会自动关闭!!!!
     */
    @PostMapping("/openRtpServerMultiplex")
    public OpenRtpServerResult openRtpServerMultiplex(@RequestBody OpenRtpServerReq req) {
        return zlmRestTemplate.openRtpServerMultiplex(req);
    }

    //    @PostMapping("/connectRtpServer")
//    public ServerResponse<OpenRtpServerResult> connectRtpServer(@RequestBody ConnectRtpServerReq req) {
//        return zlmRestTemplate.connectRtpServer(req);
//    }
//

    /**
     * http :9000/media/closeRtpServer stream_id="0100000001"
     */
    @PostMapping("/closeRtpServer")
    public CloseRtpServerResult closeRtpServer(@RequestBody CloseRtpServerReq rtpServer) {
        return zlmRestTemplate.closeRtpServer(rtpServer);
    }

    //    @PostMapping("/updateRtpServerSSRC")
//    public ServerResponse<String> updateRtpServerSSRC(@RequestBody Map<String, Object> params) {
//        return zlmRestTemplate.updateRtpServerSSRC(params.get("streamId").toString(), params.get("ssrc").toString());
//    }
//
//    @PostMapping("/pauseRtpCheck")
//    public ServerResponse<String> pauseRtpCheck(@RequestBody String streamId) {
//        return zlmRestTemplate.pauseRtpCheck(streamId);
//    }
//
//    @PostMapping("/resumeRtpCheck")
//    public ServerResponse<String> resumeRtpCheck(@RequestBody String streamId) {
//        return zlmRestTemplate.resumeRtpCheck(streamId);
//    }
//

    /**
     * http :9000/media/listRtpServer
     */
    @GetMapping("/listRtpServer")
    public List<ListRtpServerResult> listRtpServer() {
        return zlmRestTemplate.listRtpServer();
    }


    /**
     * http :9000/media/startSendRtp  vhost="__defaultVhost__" app="live" stream="test" ssrc="5001" dst_url="192.168.20.215" dst_port="50001" is_udp=true
     */
    @PostMapping("/startSendRtp")
    public StartSendRtpResult startSendRtp(@RequestBody StartSendRtpReq req) {
        return zlmRestTemplate.startSendRtp(req);
    }

    @PostMapping("/startSendRtpPassive")
    public StartSendRtpResult startSendRtpPassive(@RequestBody StartSendRtpPassiveReq req) {
        return zlmRestTemplate.startSendRtpPassive(req);
    }

    /**
     * http :9000/media/stopSendRtp  vhost="__defaultVhost__" app="live" stream="test" ssrc="5001"
     */
    @PostMapping("/stopSendRtp")
    public MediaServerResponse<Void> stopSendRtp(@RequestBody StopSendRtpReq req) {
        return zlmRestTemplate.stopSendRtp(req);
    }


    /**
     * http :9000/media/listRtpSender vhost="____defaultVhost____"  app="live" stream="test"
     */
    @PostMapping("/listRtpSender")
    public RtpSenderListResult listRtpSender(@RequestBody MediaReq req) {
        return zlmRestTemplate.listRtpSender(req);
    }
//    @PostMapping("/getProxyInfo")
//    public ServerResponse<Object> getProxyInfo(@RequestBody Map<String, Object> params) {
//        return zlmRestTemplate.getProxyInfo(params);
//    }
//
//    @PostMapping("/getProxyPusherInfo")
//    public ServerResponse<Object> getProxyPusherInfo(@RequestBody Map<String, Object> params) {
//        return zlmRestTemplate.getProxyPusherInfo(params);
//    }
//
//    @PostMapping("/startMultiMp4Publish")
//    public ServerResponse<Object> startMultiMp4Publish(@RequestBody Map<String, Object> params) {
//        return zlmRestTemplate.startMultiMp4Publish(params);
//    }
//
//    @PostMapping("/getStorageSpace")
//    public ServerResponse<Object> getStorageSpace(@RequestBody Map<String, Object> params) {
//        return zlmRestTemplate.getStorageSpace(params);
//    }
//
//    @PostMapping("/stopMultiMp4Publish")
//    public ServerResponse<Object> stopMultiMp4Publish(@RequestBody Map<String, Object> params) {
//        return zlmRestTemplate.stopMultiMp4Publish(params);
//    }
//
//    @PostMapping("/loadMP4File")
//    public ServerResponse<Object> loadMP4File(@RequestBody Map<String, Object> params) {
//        return zlmRestTemplate.loadMP4File(params);
//    }
}
