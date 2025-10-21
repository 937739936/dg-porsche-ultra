package com.shdatalink.sip.server.media;

import com.shdatalink.sip.server.media.bean.entity.DeleteRecordDirectory;
import com.shdatalink.sip.server.media.bean.entity.req.*;
import com.shdatalink.sip.server.media.bean.entity.resp.*;
import com.shdatalink.sip.server.media.interceptor.MediaHostRewriteFilter;
import com.shdatalink.sip.server.media.interceptor.MediaHttpRequestHeadersFactory;
import com.shdatalink.sip.server.media.interceptor.MediaHttpRequestLogger;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;


@Path("/index/api")
@RegisterRestClient
@RegisterClientHeaders(MediaHttpRequestHeadersFactory.class)
@RegisterProvider(MediaHostRewriteFilter.class)
@RegisterProvider(MediaHttpRequestHeadersFactory.class)
@RegisterProvider(MediaHttpRequestLogger.class)
public interface MediaHttpClient {

    /**
     * 查询版本
     * @return
     */
    @GET
    @Path("/version")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<MediaVersion> getVersion();

    /**
     * 创建RTP服务器
     */
    @POST
    @Path("/openRtpServer")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    OpenRtpServerResult openRtpServer(OpenRtpServerReq openRtpServerReq);

    /**
     * 获取主要对象个数统计，主要用于分析内存性能
     */
    @GET
    @Path("/getStatistic")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<MediaStatisticsResult> getStatistic();

    /**
     * 重启服务  未使用daemon模式仅关闭
     */
    @GET
    @Path("/restartServer")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<Void> restartServer();

    /**
     * 获取流列表，可选筛选参数
     */
    @POST
    @Path("/getMediaList")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<List<MediaListResult>> getMediaList(MediaReq req);

    /**
     * 获取所有API列表
     */
    @GET
    @Path("/getApiList")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<List<String>> getApiList();


    /**
     * 获取各epoll(或select)线程负载以及延时
     */
    @GET
    @Path("/getThreadsLoad")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<List<ThreadLoadResult>> getThreadsLoad();


    /**
     * 获取各后台epoll(或select)线程负载以及延时
     */
    @GET
    @Path("/getWorkThreadsLoad")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<List<ThreadLoadResult>> getWorkThreadsLoad();

    /**
     * 获取服务器配置
     */
    @GET
    @Path("/getServerConfig")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<List<ServerNodeConfigResult>> getServerConfig();


    /**
     * 关闭流(目前所有类型的流都支持关闭)
     */
    @POST
    @Path("/close_streams")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<CloseStreamsResult> closeStreams(CloseStreamsReq closeStreamsReq);


    /**
     * 获取所有TcpSession列表
     */
    @POST
    @Path("/getAllSession")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<List<TcpSessionResult>> getAllSession(TcpSessionReq req);

    /**
     * 断开tcp连接
     */
    @POST
    @Path("/kick_session")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<Void> kickSession(KillSessionReq req);


    /**
     * 批量断开tcp连接
     */
    @POST
    @Path("/kick_sessions")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<Void> kickSessions(KillSessionsReq req);

    /**
     * 添加代理拉流
     */
    @POST
    @Path("/addStreamProxy")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<AddStreamProxyResult> addStreamProxy(AddStreamProxyReq req);

    /**
     * 关闭拉流代理
     */
    @POST
    @Path("/delStreamProxy")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<DelStreamProxyResult> delStreamProxy(DelStreamProxyReq req);


    /**
     * 添加rtsp/rtmp推流
     */
    @POST
    @Path("/addStreamPusherProxy")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<AddStreamProxyResult> addStreamPusherProxy(AddStreamPusherReq req);


    /**
     * 关闭推流
     */
    @POST
    @Path("/delStreamPusherProxy")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<DelStreamProxyResult> delStreamPusherProxy(DelStreamProxyReq req);



    /**
     * 添加FFmpeg拉流代理
     */
    @POST
    @Path("/addFFmpegSource")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<AddStreamProxyResult> addFFmpegSource(AddFFmpegSourceReq req);

    /**
     * 关闭FFmpeg拉流代理
     */
    @POST
    @Path("/delFFmpegSource")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<DelStreamProxyResult> delFFmpegSource(DelStreamProxyReq req);

    /**
     * 获取媒体流播放器列表
     */
    @POST
    @Path("/getMediaPlayerList")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<List<MediaPlayerResult>> getMediaPlayerList(MediaReq req);

    /**
     * 获取rtp推流信息
     */
    @POST
    @Path("/getRtpInfo")
    @Produces(MediaType.APPLICATION_JSON)
    RtpInfoResult getRtpInfo(GetRtpInfoReq req);

    /**
     * 搜索文件系统，获取流对应的录像文件列表或日期文件夹列表
     */
    @POST
    @Path("/getMp4RecordFile")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<GetMp4RecordFileResult> getMp4RecordFile(GetMp4FileReq req);

    /**
     * 开始录制
     */
    @POST
    @Path("/startRecord")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<Void> startRecord(StartRecordReq req);

    /**
     * 停止录制
     */
    @POST
    @Path("/stopRecord")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<Void> stopRecord(MediaReq recordReq);

    /**
     * 是否正在录制
     */
    @POST
    @Path("/isRecording")
    @Produces(MediaType.APPLICATION_JSON)
    IsRecordingResult isRecording(MediaReq recordReq);

    /**
     * 获取截图
     */
    @POST
    @Path("/getSnap")
    @Produces(MediaType.APPLICATION_JSON)
    byte[] getSnap(SnapshotReq snapshotReq);


    /**
     * 关闭RTP服务器
     */
    @POST
    @Path("/closeRtpServer")
    @Produces(MediaType.APPLICATION_JSON)
    CloseRtpServerResult closeRtpServer(CloseRtpServerReq rtpServer);

    /**
     * 获取RTP服务器列表
     */
    @GET
    @Path("/listRtpServer")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<List<ListRtpServerResult>> listRtpServer();

    /**
     * 开始发送rtp
     */
    @POST
    @Path("/startSendRtp")
    @Produces(MediaType.APPLICATION_JSON)
    StartSendRtpResult startSendRtp(StartSendRtpReq req);

    /**
     * 开始tcp passive被动发送rtp
     */
    @POST
    @Path("/startSendRtpPassive")
    @Produces(MediaType.APPLICATION_JSON)
    StartSendRtpResult startSendRtpPassive(StartSendRtpPassiveReq req);

    /**
     * 停止发送rtp
     */
    @POST
    @Path("/stopSendRtp")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<Void> stopSendRtp(StopSendRtpReq req);


    /**
     * 广播webrtc datachannel消息
     */
    @POST
    @Path("/broadcastMessage")
    @Produces(MediaType.APPLICATION_JSON)
    MediaServerResponse<Void> broadcastMessage(BroadcastMessageReq req);


    /**
     * 删除录像文件夹
     */
    @POST
    @Path("/deleteRecordDirectory")
    @Produces(MediaType.APPLICATION_JSON)
    DeleteRecordDirectory deleteRecordDirectory(DeleteRecordDictionaryReq req);

    /**
     * 连接RTP服务器
     */
    @POST
    @Path("/connectRtpServer")
    @Produces(MediaType.APPLICATION_JSON)
    OpenRtpServerResult connectRtpServer(ConnectRtpServerReq req);


    /**
     * 创建多路复用RTP服务器
     */
    @POST
    @Path("/openRtpServerMultiplex")
    @Produces(MediaType.APPLICATION_JSON)
    OpenRtpServerResult openRtpServerMultiplex(OpenRtpServerReq req);

    /**
     * 获取Rtp发送列表
     */
    @POST
    @Path("/listRtpSender")
    @Produces(MediaType.APPLICATION_JSON)
    RtpSenderListResult listRtpSender(MediaReq req);
}
