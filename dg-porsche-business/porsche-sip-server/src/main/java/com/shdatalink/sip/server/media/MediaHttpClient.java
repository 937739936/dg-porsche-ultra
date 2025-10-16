package com.shdatalink.sip.server.media;

import com.shdatalink.sip.server.config.SipConfigProperties;
import com.shdatalink.sip.server.media.bean.constant.ApiConstants;
import com.shdatalink.sip.server.media.bean.entity.DeleteRecordDirectory;
import com.shdatalink.sip.server.media.bean.entity.req.*;
import com.shdatalink.sip.server.media.bean.entity.resp.*;
import com.shdatalink.sip.server.media.interceptor.MediaHttpRequestInterceptor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Slf4j
@Component
public class MediaHttpClient {

    @Autowired
    private SipConfigProperties sipConfigProperties;

    private String mediaUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MediaHttpRequestInterceptor mediaHttpRequestInterceptor;

    @PostConstruct
    public void init() {
        restTemplate.getInterceptors().add(mediaHttpRequestInterceptor);
        this.mediaUrl = "http://" + sipConfigProperties.getMedia().getIp() + ":" + sipConfigProperties.getMedia().getPort();
    }

    /**
     * 获取版本信息
     */
    public MediaVersion getVersion() {
        String url = mediaUrl + ApiConstants.GET_VERSION;
        ResponseEntity<MediaServerResponse<MediaVersion>> exchange = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<MediaVersion> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("获取media服务版本信息出错:{}", mediaServerResponse);
            throw new RuntimeException("获取media服务版本信息出错:" + mediaServerResponse.getMsg());
        }
    }

    /**
     * 创建RTP服务器
     */
    public OpenRtpServerResult openRtpServer(OpenRtpServerReq openRtpServerReq) {
        String url = mediaUrl + ApiConstants.OPEN_RTP_SERVER;
        RequestEntity<OpenRtpServerReq> request = new RequestEntity<>(openRtpServerReq, HttpMethod.POST, URI.create(url));
        ResponseEntity<OpenRtpServerResult> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        OpenRtpServerResult serverResponse = exchange.getBody();
        if (serverResponse.getCode() == 0) {
            return serverResponse;
        } else {
            log.error("创建RTP服务器出错:{}", serverResponse);
            throw new RuntimeException("创建RTP服务器出错:" + serverResponse.getMsg());
        }
    }

    /**
     * 获取主要对象个数统计，主要用于分析内存性能
     */
    public MediaStatisticsResult getStatistic() {
        String url = mediaUrl + ApiConstants.GET_STATISTIC;
        ResponseEntity<MediaServerResponse<MediaStatisticsResult>> exchange = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<MediaStatisticsResult> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("获取media服务主要对象个数出错:{}", mediaServerResponse);
            throw new RuntimeException("获取media服务主要对象个数出错:" + mediaServerResponse.getMsg());
        }
    }

    /**
     * 重启服务  未使用daemon模式仅关闭
     */
    public MediaServerResponse<Void> restartServer() {
        String url = mediaUrl + ApiConstants.RESTART_SERVER;
        ResponseEntity<MediaServerResponse<MediaServerResponse<Void>>> exchange = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<MediaServerResponse<Void>> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("获取media服务后台线程负载出错:{}", mediaServerResponse);
            throw new RuntimeException("获取media服务后台线程负载出错:" + mediaServerResponse.getMsg());
        }
    }

    /**
     * 获取流列表，可选筛选参数
     */
    public List<MediaListResult> getMediaList(String stream) {
        String url = mediaUrl + ApiConstants.GET_MEDIA_LIST;
        RequestEntity<MediaReq> request = new RequestEntity<>(new MediaReq(stream), HttpMethod.GET, URI.create(url));
        ResponseEntity<MediaServerResponse<List<MediaListResult>>> exchange = restTemplate.exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<List<MediaListResult>> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData() == null ? new ArrayList<>() : mediaServerResponse.getData();
        } else {
            log.error("获取流列表:{}", mediaServerResponse);
            throw new RuntimeException("获取流列表:" + mediaServerResponse.getMsg());
        }
    }

    /**
     * 获取流列表，可选筛选参数
     */
    public List<MediaListResult> getMediaList() {
        return getMediaList(null);
    }

    public boolean mediaExists(String stream) {
        List<MediaListResult> mediaList = getMediaList(stream);
        return mediaList
                .stream()
                .anyMatch(item -> {
                    if(item == null){
                        return false;
                    }
                   return Objects.equals(stream, item.getStream());
                });
    }

    /**
     * 获取所有API列表
     */
    public List<String> getApiList() {
        String url = mediaUrl + ApiConstants.GET_API_LIST;
        ResponseEntity<MediaServerResponse<List<String>>> exchange = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<List<String>> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("获取API列表出错:{}", mediaServerResponse);
            throw new RuntimeException("获取API列表出错:" + mediaServerResponse.getMsg());
        }
    }

    /**
     * 获取各epoll(或select)线程负载以及延时
     */
    public List<ThreadLoadResult> getThreadsLoad() {
        String url = mediaUrl + ApiConstants.GET_THREADS_LOAD;
        ResponseEntity<MediaServerResponse<List<ThreadLoadResult>>> exchange = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<MediaServerResponse<List<ThreadLoadResult>>>() {
        });
        MediaServerResponse<List<ThreadLoadResult>> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("获取网络线程负载出错:{}", mediaServerResponse);
            throw new RuntimeException("获取网络线程负载出错:" + mediaServerResponse.getMsg());
        }
    }

    /**
     * 获取各后台epoll(或select)线程负载以及延时
     */
    public List<ThreadLoadResult> getWorkThreadsLoad() {
        String url = mediaUrl + ApiConstants.GET_WORK_THREADS_LOAD;
        ResponseEntity<MediaServerResponse<List<ThreadLoadResult>>> exchange = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<List<ThreadLoadResult>> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("获取后台线程负载出错:{}", mediaServerResponse);
            throw new RuntimeException("获取后台线程负载出错:" + mediaServerResponse.getMsg());
        }
    }

    /**
     * 获取服务器配置
     */
    public List<ServerNodeConfigResult> getServerConfig() {
        String url = mediaUrl + ApiConstants.GET_SERVER_CONFIG;
        ResponseEntity<MediaServerResponse<List<ServerNodeConfigResult>>> exchange = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<List<ServerNodeConfigResult>> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("获取服务器配置出错:{}", mediaServerResponse);
            throw new RuntimeException("获取服务器配置出错:" + mediaServerResponse.getMsg());
        }
    }

    /**
     * 关闭流(目前所有类型的流都支持关闭)
     */
    public CloseStreamsResult closeStreams(CloseStreamsReq closeStreamsReq) {
        String url = mediaUrl + ApiConstants.CLOSE_STREAMS;
        RequestEntity<CloseStreamsReq> request = new RequestEntity<>(closeStreamsReq, HttpMethod.POST, URI.create(url));
        ResponseEntity<MediaServerResponse<CloseStreamsResult>> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<CloseStreamsResult> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("关闭流出错:{}", mediaServerResponse);
            throw new RuntimeException("关闭流出错:" + mediaServerResponse.getMsg());
        }
    }

    /**
     * 获取所有TcpSession列表
     */
    public List<TcpSessionResult> getAllSession(TcpSessionReq req) {
        String url = mediaUrl + ApiConstants.GET_ALL_SESSION;
        RequestEntity<TcpSessionReq> request = new RequestEntity<>(req, HttpMethod.POST, URI.create(url));
        ResponseEntity<MediaServerResponse<List<TcpSessionResult>>> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<List<TcpSessionResult>> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("获取所有TcpSession列表出错:{}", mediaServerResponse);
            throw new RuntimeException("获取所有TcpSession列表出错:" + mediaServerResponse.getMsg());
        }
    }

    /**
     * 断开tcp连接
     */
    public void kickSession(KillSessionReq killSessionReq) {
        String url = mediaUrl + ApiConstants.KICK_SESSION;
        RequestEntity<KillSessionReq> request = new RequestEntity<>(killSessionReq, HttpMethod.POST, URI.create(url));
        ResponseEntity<MediaServerResponse<Void>> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<Void> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() != 0) {
            log.error("断开tcp连接出错:{}", mediaServerResponse);
            throw new RuntimeException("断开tcp连接出错:" + mediaServerResponse.getMsg());
        }
    }

    /**
     * 批量断开tcp连接
     */
    public void kickSessions(KillSessionsReq killSessionReq) {
        String url = mediaUrl + ApiConstants.KICK_SESSIONS;
        RequestEntity<KillSessionsReq> request = new RequestEntity<>(killSessionReq, HttpMethod.POST, URI.create(url));
        ResponseEntity<MediaServerResponse<Void>> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<Void> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() != 0) {
            log.error("断开tcp连接出错:{}", mediaServerResponse);
            throw new RuntimeException("断开tcp连接出错:" + mediaServerResponse.getMsg());
        }
    }

    /**
     * 添加代理拉流
     */
    public AddStreamProxyResult addStreamProxy(AddStreamProxyReq addStreamProxyReq) {
        String url = mediaUrl + ApiConstants.ADD_STREAM_PROXY;
        RequestEntity<AddStreamProxyReq> request = new RequestEntity<>(addStreamProxyReq, HttpMethod.POST, URI.create(url));
        ResponseEntity<MediaServerResponse<AddStreamProxyResult>> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<AddStreamProxyResult> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            if(mediaServerResponse.getCode() == -1 && "This stream already exists".equals(mediaServerResponse.getMsg())){
                return new AddStreamProxyResult(addStreamProxyReq.getStream());
            }else{
                log.error("添加代理拉流出错:{}", mediaServerResponse);
                throw new RuntimeException("添加代理拉流出错:" + mediaServerResponse.getMsg());
            }
        }
    }

    /**
     * 关闭拉流代理
     */
    public DelStreamProxyResult delStreamProxy(DelStreamProxyReq key) {
        String url = mediaUrl + ApiConstants.DEL_STREAM_PROXY;
        RequestEntity<DelStreamProxyReq> request = new RequestEntity<>(key, HttpMethod.POST, URI.create(url));
        ResponseEntity<MediaServerResponse<DelStreamProxyResult>> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<DelStreamProxyResult> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("关闭拉流代理出错:{}", mediaServerResponse);
            throw new RuntimeException("关闭拉流代理出错:" + mediaServerResponse.getMsg());
        }
    }

    /**
     * 添加rtsp/rtmp推流
     */
    public AddStreamProxyResult addStreamPusherProxy(AddStreamPusherReq streamPusherItem) {
        String url = mediaUrl + ApiConstants.ADD_STREAM_PUSHER_PROXY;
        RequestEntity<AddStreamPusherReq> request = new RequestEntity<>(streamPusherItem, HttpMethod.POST, URI.create(url));
        ResponseEntity<MediaServerResponse<AddStreamProxyResult>> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<AddStreamProxyResult> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("添加rtsp/rtmp推流出错:{}", mediaServerResponse);
            throw new RuntimeException("添加rtsp/rtmp推流出错:" + mediaServerResponse.getMsg());
        }
    }

    /**
     * 关闭推流
     */
    public DelStreamProxyResult delStreamPusherProxy(DelStreamProxyReq key) {
        String url = mediaUrl + ApiConstants.DEL_STREAM_PUSHER_PROXY;
        RequestEntity<DelStreamProxyReq> request = new RequestEntity<>(key, HttpMethod.POST, URI.create(url));
        ResponseEntity<MediaServerResponse<DelStreamProxyResult>> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<DelStreamProxyResult> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("关闭推流出错:{}", mediaServerResponse);
            throw new RuntimeException("关闭推流出错:" + mediaServerResponse.getMsg());
        }
    }


    /**
     * 添加FFmpeg拉流代理
     */
    public AddStreamProxyResult addFFmpegSource(AddFFmpegSourceReq addFFmpegSourceReq) {
        String url = mediaUrl + ApiConstants.ADD_FFMPEG_SOURCE;
        RequestEntity<AddFFmpegSourceReq> request = new RequestEntity<>(addFFmpegSourceReq, HttpMethod.POST, URI.create(url));
        ResponseEntity<MediaServerResponse<AddStreamProxyResult>> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<AddStreamProxyResult> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("添加FFmpeg拉流代理出错:{}", mediaServerResponse);
            throw new RuntimeException("添加FFmpeg拉流代理出错:" + mediaServerResponse.getMsg());
        }
    }

    /**
     * 关闭FFmpeg拉流代理
     */
    public DelStreamProxyResult delFFmpegSource(DelStreamProxyReq key) {
        String url = mediaUrl + ApiConstants.DEL_FFMPEG_SOURCE;
        RequestEntity<DelStreamProxyReq> request = new RequestEntity<>(key, HttpMethod.POST, URI.create(url));
        ResponseEntity<MediaServerResponse<DelStreamProxyResult>> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<DelStreamProxyResult> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("关闭FFmpeg拉流代理出错:{}", mediaServerResponse);
            throw new RuntimeException("关闭FFmpeg拉流代理出错:" + mediaServerResponse.getMsg());
        }
    }


    /**
     * 获取媒体流播放器列表
     */
    public List<MediaPlayerResult> getMediaPlayerList(MediaReq mediaReq) {
        String url = mediaUrl + ApiConstants.GET_MEDIA_PLAYER_LIST;
        RequestEntity<MediaReq> request = new RequestEntity<>(mediaReq, HttpMethod.POST, URI.create(url));
        ResponseEntity<MediaServerResponse<List<MediaPlayerResult>>> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<List<MediaPlayerResult>> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("获取媒体流播放器列表出错:{}", mediaServerResponse);
            throw new RuntimeException("获取媒体流播放器列表出错:" + mediaServerResponse.getMsg());
        }
    }


    /**
     * 获取rtp推流信息
     */
    public RtpInfoResult getRtpInfo(GetRtpInfoReq req) {
        String url = mediaUrl + ApiConstants.GET_RTP_INFO;
        RequestEntity<GetRtpInfoReq> request = new RequestEntity<>(req, HttpMethod.POST, URI.create(url));
        ResponseEntity<RtpInfoResult> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        RtpInfoResult serverResponse = exchange.getBody();
        MediaServerResponse<RtpInfoResult> rtpInfoResultMediaServerResponse = new MediaServerResponse<RtpInfoResult>();
        rtpInfoResultMediaServerResponse.setData(serverResponse);
        if (serverResponse.getCode() == 0) {
            return rtpInfoResultMediaServerResponse.getData();
        } else {
            log.error("获取rtp推流信息出错:{}", serverResponse);
            throw new RuntimeException("获取rtp推流信息出错:" + rtpInfoResultMediaServerResponse.getMsg());
        }
    }


    /**
     * 搜索文件系统，获取流对应的录像文件列表或日期文件夹列表
     */
    public GetMp4RecordFileResult getMp4RecordFile(GetMp4FileReq getMp4FileReq) {
        String url = mediaUrl + ApiConstants.GET_MP4_RECORD_FILE;
        RequestEntity<GetMp4FileReq> request = new RequestEntity<>(getMp4FileReq, HttpMethod.POST, URI.create(url));
        ResponseEntity<MediaServerResponse<GetMp4RecordFileResult>> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<GetMp4RecordFileResult> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("获取流信息出错:{}", mediaServerResponse);
            throw new RuntimeException("获取流信息出错:" + mediaServerResponse.getMsg());
        }
    }

    /**
     * 开始录制
     */
    public MediaServerResponse<Void> startRecord(StartRecordReq recordReq) {
        String url = mediaUrl + ApiConstants.START_RECORD;
        RequestEntity<StartRecordReq> request = new RequestEntity<>(recordReq, HttpMethod.POST, URI.create(url));
        ResponseEntity<MediaServerResponse<Void>> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        return exchange.getBody();
    }
//

    /**
     * 停止录制
     */
    public MediaServerResponse<Void> stopRecord(MediaReq recordReq) {
        String url = mediaUrl + ApiConstants.STOP_RECORD;
        RequestEntity<MediaReq> request = new RequestEntity<>(recordReq, HttpMethod.POST, URI.create(url));
        ResponseEntity<MediaServerResponse<Void>> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        return exchange.getBody();
    }

    /**
     * 是否正在录制
     */
    public IsRecordingResult isRecording(MediaReq recordReq) {
        String url = mediaUrl + ApiConstants.IS_RECORDING;
        RequestEntity<MediaReq> request = new RequestEntity<>(recordReq, HttpMethod.POST, URI.create(url));
        ResponseEntity<IsRecordingResult> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        return exchange.getBody();
    }

    /**
     * 获取截图
     */
    public byte[] getSnap(SnapshotReq snapshotReq) {
        String url = mediaUrl + ApiConstants.GET_SNAP;
        RequestEntity<SnapshotReq> request = new RequestEntity<>(snapshotReq, HttpMethod.POST, URI.create(url));
        ResponseEntity<byte[]> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        return exchange.getBody();
    }


    /**
     * 关闭RTP服务器
     */
    public CloseRtpServerResult closeRtpServer(CloseRtpServerReq rtpServer) {
        String url = mediaUrl + ApiConstants.CLOSE_RTP_SERVER;
        RequestEntity<CloseRtpServerReq> request = new RequestEntity<>(rtpServer, HttpMethod.POST, URI.create(url));
        ResponseEntity<CloseRtpServerResult> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        return exchange.getBody();
    }

    /**
     * 获取RTP服务器列表
     */
    public List<ListRtpServerResult> listRtpServer() {
        String url = mediaUrl + ApiConstants.LIST_RTP_SERVER;
        ResponseEntity<MediaServerResponse<List<ListRtpServerResult>>> exchange = restTemplate.exchange(url, HttpMethod.POST, null, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<List<ListRtpServerResult>> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("获取RTP服务器列表出错:{}", mediaServerResponse);
            throw new RuntimeException("获取RTP服务器列表出错:" + mediaServerResponse.getMsg());
        }
    }

    /**
     * 开始发送rtp
     */
    public StartSendRtpResult startSendRtp(StartSendRtpReq req) {
        String url = mediaUrl + ApiConstants.START_SEND_RTP;
        RequestEntity<StartSendRtpReq> request = new RequestEntity<>(req, HttpMethod.POST, URI.create(url));
        ResponseEntity<MediaServerResponse<StartSendRtpResult>> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<StartSendRtpResult> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("开始发送rtp出错:{}", mediaServerResponse);
            throw new RuntimeException("开始发送rtp出错:" + mediaServerResponse.getMsg());
        }
    }

    /**
     * 开始tcp passive被动发送rtp
     */
    public StartSendRtpResult startSendRtpPassive(StartSendRtpPassiveReq req) {
        String url = mediaUrl + ApiConstants.START_SEND_RTP_PASSIVE;
        RequestEntity<StartSendRtpPassiveReq> request = new RequestEntity<>(req, HttpMethod.POST, URI.create(url));
        ResponseEntity<MediaServerResponse<StartSendRtpResult>> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<StartSendRtpResult> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("开始tcp passive被动发送rtp出错:{}", mediaServerResponse);
            throw new RuntimeException("开始tcp passive被动发送rtp出错:" + mediaServerResponse.getMsg());
        }
    }

    /**
     * 停止发送rtp
     */
    public MediaServerResponse<Void> stopSendRtp(StopSendRtpReq req) {
        String url = mediaUrl + ApiConstants.STOP_SEND_RTP;
        RequestEntity<StopSendRtpReq> request = new RequestEntity<>(req, HttpMethod.POST, URI.create(url));
        ResponseEntity<MediaServerResponse<Void>> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        return exchange.getBody();
    }


    /**
     * 广播webrtc datachannel消息
     */
    public MediaServerResponse<Void> broadcastMessage(BroadcastMessageReq req) {
        String url = mediaUrl + ApiConstants.BROADCAST_MESSAGE;
        RequestEntity<BroadcastMessageReq> request = new RequestEntity<>(req, HttpMethod.POST, URI.create(url));
        ResponseEntity<MediaServerResponse<Void>> exchange = restTemplate.exchange(url, HttpMethod.POST, request,
                new ParameterizedTypeReference<>() {
                }
        );
        MediaServerResponse<Void> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse;
        } else {
            log.error("广播webrtc datachannel消息出错:{}", mediaServerResponse);
            throw new RuntimeException("广播webrtc datachannel消息出错:" + mediaServerResponse.getMsg());
        }
    }


    /**
     * 删除录像文件夹
     */
    public DeleteRecordDirectory deleteRecordDirectory(DeleteRecordDictionaryReq req) {
        String url = mediaUrl + ApiConstants.DELETE_RECORD_DIRECTORY;
        RequestEntity<DeleteRecordDictionaryReq> request = new RequestEntity<>(req, HttpMethod.POST, URI.create(url));
        ResponseEntity<MediaServerResponse<DeleteRecordDirectory>> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<DeleteRecordDirectory> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("删除录像文件夹出错:{}", mediaServerResponse);
            throw new RuntimeException("删除录像文件夹出错:" + mediaServerResponse.getMsg());
        }
    }

//
//    /**
//     * 点播mp4文件
//     */
//    public Object loadMP4File(Map<String, Object> params) {
//        String url = mediaUrl + ApiConstants.LOAD_MP4_FILE;
//        RequestEntity request = new RequestEntity<>(req, HttpMethod.POST, URI.create(url));
//        ResponseEntity<ServerResponse<Object>> exchange = restTemplate.exchange(
//                url,
//                HttpMethod.POST,
//                request,
//                new ParameterizedTypeReference<ServerResponse<Object>>() {
//                }
//        );
//        ServerResponse<Object> serverResponse = exchange.getBody();
//        if (serverResponse.getCode() == 0) {
//            return serverResponse.getData();
//        } else {
//            log.error("点播mp4文件出错:{}", serverResponse);
//            throw new RuntimeException("点播mp4文件出错:" + serverResponse.getMsg());
//        }
//    }
//
//
//    /**
//     * 更新RTP服务器过滤SSRC
//     */
//    public String updateRtpServerSSRC(String streamId, String ssrc) {
//        Map<String, Object> params = new HashMap<>();
//        params.put("stream_id", streamId);
//        params.put("ssrc", ssrc);
//        String url = mediaUrl + ApiConstants.UPDATE_RTP_SERVER_SSRC;
//        RequestEntity request = new RequestEntity<>(req, HttpMethod.POST, URI.create(url));
//        ResponseEntity<ServerResponse<String>> exchange = restTemplate.exchange(
//                url,
//                HttpMethod.POST,
//                request,
//                new ParameterizedTypeReference<>() {
//                }
//        );
//        ServerResponse<String> serverResponse = exchange.getBody();
//        if (serverResponse.getCode() == 0) {
//            return serverResponse.getData();
//        } else {
//            log.error("更新RTP服务器过滤SSRC出错:{}", serverResponse);
//            throw new RuntimeException("更新RTP服务器过滤SSRC出错:" + serverResponse.getMsg());
//        }
//    }


    /**
     * 连接RTP服务器
     */
    public OpenRtpServerResult connectRtpServer(ConnectRtpServerReq req) {
        String url = mediaUrl + ApiConstants.CONNECT_RTP_SERVER;
        RequestEntity<ConnectRtpServerReq> request = new RequestEntity<>(req, HttpMethod.POST, URI.create(url));
        ResponseEntity<MediaServerResponse<OpenRtpServerResult>> exchange = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                }
        );
        MediaServerResponse<OpenRtpServerResult> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("连接RTP服务器出错:{}", mediaServerResponse);
            throw new RuntimeException("连接RTP服务器出错:" + mediaServerResponse.getMsg());
        }
    }


    /**
     * 创建多路复用RTP服务器
     */
    public OpenRtpServerResult openRtpServerMultiplex(OpenRtpServerReq req) {
        String url = mediaUrl + ApiConstants.OPEN_RTP_SERVER_MULTIPLEX;
        RequestEntity<OpenRtpServerReq> request = new RequestEntity<>(req, HttpMethod.POST, URI.create(url));
        ResponseEntity<MediaServerResponse<OpenRtpServerResult>> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        MediaServerResponse<OpenRtpServerResult> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("创建多路复用RTP服务器出错:{}", mediaServerResponse);
            throw new RuntimeException("创建多路复用RTP服务器出错:" + mediaServerResponse.getMsg());
        }
    }

    /**
     * 获取Rtp发送列表
     */
    public RtpSenderListResult listRtpSender(MediaReq req) {
        String url = mediaUrl + ApiConstants.LIST_RTP_SENDER;
        RequestEntity<MediaReq> request = new RequestEntity<>(req, HttpMethod.POST, URI.create(url));
        ResponseEntity<RtpSenderListResult> exchange = restTemplate.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<>() {
        });
        RtpSenderListResult serverResponse = exchange.getBody();
        if (serverResponse.getCode() == 0) {
            return serverResponse;
        } else {
            log.error("获取Rtp发送列表出错:{}", serverResponse);
            throw new RuntimeException("获取Rtp发送列表出错:" + serverResponse.getMsg());
        }
    }

//
//    /**
//     * 暂停RTP超时检查
//     */
//    public String pauseRtpCheck(String streamId) {
//        Map<String, Object> params = new HashMap<>();
//        params.put("stream_id", streamId);
//        String url = mediaUrl + ApiConstants.PAUSE_RTP_CHECK;
//        RequestEntity request = new RequestEntity<>(req, HttpMethod.POST, URI.create(url));
//        ResponseEntity<ServerResponse<String>> exchange = restTemplate.exchange(
//                url,
//                HttpMethod.POST,
//                request,
//                new ParameterizedTypeReference<ServerResponse<String>>() {
//                }
//        );
//        ServerResponse<String> serverResponse = exchange.getBody();
//        if (serverResponse.getCode() == 0) {
//            return serverResponse.getData();
//        } else {
//            log.error("暂停RTP超时检查出错:{}", serverResponse);
//            throw new RuntimeException("暂停RTP超时检查出错:" + serverResponse.getMsg());
//        }
//    }
//
//    /**
//     * 恢复RTP超时检查
//     */
//    public String resumeRtpCheck(String streamId) {
//        Map<String, Object> params = new HashMap<>();
//        params.put("stream_id", streamId);
//        String url = mediaUrl + ApiConstants.RESUME_RTP_CHECK;
//        RequestEntity request = new RequestEntity<>(req, HttpMethod.POST, URI.create(url));
//        ResponseEntity<ServerResponse<String>> exchange = restTemplate.exchange(
//                url,
//                HttpMethod.POST,
//                request,
//                new ParameterizedTypeReference<ServerResponse<String>>() {
//                }
//        );
//        ServerResponse<String> serverResponse = exchange.getBody();
//        if (serverResponse.getCode() == 0) {
//            return serverResponse.getData();
//        } else {
//            log.error("恢复RTP超时检查出错:{}", serverResponse);
//            throw new RuntimeException("恢复RTP超时检查出错:" + serverResponse.getMsg());
//        }
//    }


    /**
     * 设置录像流播放位置
     */
    public String seekRecordStamp(StartRecordReq recordReq) {
        String url = mediaUrl + ApiConstants.SEEK_RECORD_STAMP;
        RequestEntity<StartRecordReq> request = new RequestEntity<>(recordReq, HttpMethod.POST, URI.create(url));
        ResponseEntity<MediaServerResponse<String>> exchange = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                }
        );
        MediaServerResponse<String> mediaServerResponse = exchange.getBody();
        if (mediaServerResponse.getCode() == 0) {
            return mediaServerResponse.getData();
        } else {
            log.error("设置录像流播放位置出错:{}", mediaServerResponse);
            throw new RuntimeException("设置录像流播放位置出错:" + mediaServerResponse.getMsg());
        }
    }


//
//    /**
//     * 获取拉流代理信息
//     */
//    public Object getProxyInfo(Map<String, Object> params) {
//        String url = mediaUrl + ApiConstants.GET_PROXY_INFO;
//        RequestEntity request = new RequestEntity<>(req, HttpMethod.POST, URI.create(url));
//        ResponseEntity<ServerResponse<Object>> exchange = restTemplate.exchange(
//                url,
//                HttpMethod.POST,
//                request,
//                new ParameterizedTypeReference<ServerResponse<Object>>() {
//                }
//        );
//        ServerResponse<Object> serverResponse = exchange.getBody();
//        if (serverResponse.getCode() == 0) {
//            return serverResponse.getData();
//        } else {
//            log.error("获取拉流代理信息出错:{}", serverResponse);
//            throw new RuntimeException("获取拉流代理信息出错:" + serverResponse.getMsg());
//        }
//    }
//
//    /**
//     * 获取推流代理信息
//     */
//    public Object getProxyPusherInfo(Map<String, Object> params) {
//        String url = mediaUrl + ApiConstants.GET_PROXY_PUSHER_INFO;
//        RequestEntity request = new RequestEntity<>(req, HttpMethod.POST, URI.create(url));
//        ResponseEntity<ServerResponse<Object>> exchange = restTemplate.exchange(
//                url,
//                HttpMethod.POST,
//                request,
//                new ParameterizedTypeReference<ServerResponse<Object>>() {
//                }
//        );
//        ServerResponse<Object> serverResponse = exchange.getBody();
//        if (serverResponse.getCode() == 0) {
//            return serverResponse.getData();
//        } else {
//            log.error("获取推流代理信息出错:{}", serverResponse);
//            throw new RuntimeException("获取推流代理信息出错:" + serverResponse.getMsg());
//        }
//    }
}
