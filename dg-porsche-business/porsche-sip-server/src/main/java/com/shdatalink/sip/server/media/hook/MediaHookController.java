package com.shdatalink.sip.server.media.hook;

import com.shdatalink.framework.common.result.IgnoredResultWrapper;
import com.shdatalink.sip.server.config.annotation.Anonymous;
import com.shdatalink.sip.server.media.hook.req.*;
import com.shdatalink.sip.server.media.hook.resp.*;
import com.shdatalink.sip.server.media.hook.service.MediaHookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/media/hook")
@Anonymous
public class MediaHookController {

    @Autowired
    private MediaHookService mediaHookService;

    /**
     * 流量统计事件，播放器或推流器断开时并且耗用流量超过特定阈值时会触发此事件，阈值通过配置文件general.flowThreshold配置；此事件对回复不敏感。
     */
    @PostMapping("/on_flow_report")
    @IgnoredResultWrapper
    public HookResp flowReport(@RequestBody FlowReportReq flowReportReq) {
        return  mediaHookService.flowReport(flowReportReq);
    }

    /**
     * 访问http文件服务器上hls之外的文件时触发。
     */
    @PostMapping("/on_http_access")
    @IgnoredResultWrapper
    public HttpAccessResp httpAccess(@RequestBody HttpAccessReq httpAccessReq) {
        return mediaHookService.httpAccess(httpAccessReq);
    }

    /**
     * 播放器鉴权事件，rtsp/rtmp/http-flv/ws-flv/hls的播放都将触发此鉴权事件；
     * 如果流不存在，那么先触发on_play事件然后触发on_stream_not_found事件。
     * 播放rtsp流时，如果该流启动了rtsp专属鉴权(on_rtsp_realm)那么将不再触发on_play事件。
     */
    @PostMapping("/on_play")
    @IgnoredResultWrapper
    public HookResp play(@RequestBody PlayReq playReq) {
        return mediaHookService.play(playReq);
    }

    /**
     * rtsp/rtmp/rtp推流鉴权事件。
     */
    @PostMapping("/on_publish")
    @IgnoredResultWrapper
    public PublishResp publish(@RequestBody PublishReq publishReq) {
        return mediaHookService.publish(publishReq);
    }

    /**
     * 录制mp4完成后通知事件；此事件对回复不敏感。
     */
    @PostMapping("/on_record_mp4")
    @IgnoredResultWrapper
    public HookResp recordMp4(@RequestBody RecordMp4Req recordMp4Req) {
        return mediaHookService.recordMp4(recordMp4Req);
    }

    /**
     * 该rtsp流是否开启rtsp专用方式的鉴权事件，开启后才会触发on_rtsp_auth事件。
     * 需要指出的是rtsp也支持url参数鉴权，它支持两种方式鉴权。
     */
    @PostMapping("/on_rtsp_realm")
    @IgnoredResultWrapper
    public RtspRealmResp rtspRealm(@RequestBody RtspRealmReq rtspRealmReq) {
        return mediaHookService.rtspRealm(rtspRealmReq);
    }

    /**
     * rtsp专用的鉴权事件，先触发on_rtsp_realm事件然后才会触发on_rtsp_auth事件。
     */
    @PostMapping("/on_rtsp_auth")
    @IgnoredResultWrapper
    public RtspAuthResp rtspAuth(@RequestBody RtspAuthReq rtspAuthReq) {
        return mediaHookService.rtspAuth(rtspAuthReq);
    }

    /**
     * shell登录鉴权，ZLMediaKit提供简单的telnet调试方式
     * 使用telnet 127.0.0.1 9000能进入MediaServer进程的shell界面。
     */
    @PostMapping("/on_shell_login")
    @IgnoredResultWrapper
    public HookResp shellLogin(@RequestBody ShellLoginReq shellLoginReq) {
        return mediaHookService.shellLogin(shellLoginReq);
    }

    /**
     * rtsp/rtmp流注册或注销时触发此事件；此事件对回复不敏感。
     */
    @PostMapping("/on_stream_changed")
    @IgnoredResultWrapper
    public HookResp streamChanged(@RequestBody StreamChangedReq streamChangedReq) {
        return mediaHookService.streamChanged(streamChangedReq);
    }

    /**
     * 流无人观看时事件，用户可以通过此事件选择是否关闭无人看的流。
     * 一个直播流注册上线了，如果一直没人观看也会触发一次无人观看事件，触发时的协议schema是随机的，看哪种协议最晚注册(一般为hls)。
     * 后续从有人观看转为无人观看，触发协议schema为最后一名观看者使用何种协议。
     * 目前mp4/hls录制不当做观看人数(mp4录制可以通过配置文件mp4_as_player控制，但是rtsp/rtmp/rtp转推算观看人数，也会触发该事件。
     */
    @PostMapping("/on_stream_none_reader")
    @IgnoredResultWrapper
    public StreamNoneReaderResp streamNoneReader(@RequestBody StreamNoneReaderReq streamNoneReaderReq) {
        return mediaHookService.streamNoneReader(streamNoneReaderReq);
    }

    /*
     * 流未找到事件，用户可以在此事件触发时，立即去拉流，这样可以实现按需拉流；此事件对回复不敏感。
     */
    @PostMapping("/on_stream_not_found")
    @IgnoredResultWrapper
    public HookResp streamNotFound(@RequestBody StreamNotFoundReq streamNotFoundReq) {
        return mediaHookService.streamNotFound(streamNotFoundReq);
    }

    /**
     * 服务器启动事件，可以用于监听服务器崩溃重启；此事件对回复不敏感。
     */
    @PostMapping("/on_server_started")
    @IgnoredResultWrapper
    public HookResp serverStarted(@RequestBody ServerStartedReqResult serverStartedReq) {
        return mediaHookService.serverStarted(serverStartedReq);
    }

    /**
     * 服务器定时上报，上报间隔可配置，默认10s上报一次
     */
    @PostMapping("/on_server_keepalive")
    @IgnoredResultWrapper
    public HookResp onServerKeepalive(@RequestBody ServerKeepaliveReq serverKeepaliveReq) {
        return mediaHookService.onServerKeepalive(serverKeepaliveReq);
    }

    /**
     * 调用openRtpServer 接口，rtp server 长时间未收到数据,执行此web hook,对回复不敏感
     */
    @PostMapping("/on_rtp_server_timeout")
    @IgnoredResultWrapper
    public HookResp onRtpServerTimeout(@RequestBody RtpServerTimeoutReq rtpServerTimeoutReq) {
        return mediaHookService.onRtpServerTimeout(rtpServerTimeoutReq);
    }

    @PostMapping("/on_server_exited")
    @IgnoredResultWrapper
    public HookResp onServerExited(@RequestBody ServerExitedReqResult req) {
        return mediaHookService.onServerExited(req);
    }

}
