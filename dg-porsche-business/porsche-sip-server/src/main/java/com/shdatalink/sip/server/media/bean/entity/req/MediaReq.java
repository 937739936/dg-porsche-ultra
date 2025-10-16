package com.shdatalink.sip.server.media.bean.entity.req;

import com.shdatalink.sip.server.media.bean.constant.MediaEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;


@Data
@NoArgsConstructor
public class MediaReq {

    /**
     * 筛选协议，例如 rtsp或rtmp
     */
    protected String schema = "rtsp";
    /**
     * 筛选虚拟主机，例如__defaultVhost__
     */
    protected String vhost = "__defaultVhost__";
    /**
     * 筛选应用名，例如 live
     */
    protected String app = "rtp";
    /**
     * 筛选流id，例如 test
     */
    protected String stream;

    public MediaReq(String stream) {
        this.stream = stream;
    }

    /**
     * 获取RTP实例
     *
     * @param stream 流
     * @param clazz  clazz
     * @return {@link T}
     */
    public static <T extends MediaReq> T getRtpInstance(String stream, Class<T> clazz) {
        return getInstance(MediaEnum.App.rtp.name(), stream, clazz);
    }

    /**
     * 获取代理实例
     *
     * @param stream 流
     * @param clazz  clazz
     * @return {@link T}
     */
    public static <T extends MediaReq> T getProxyInstance(String stream, Class<T> clazz) {
        return getInstance(MediaEnum.App.proxy.name(), stream, clazz);
    }

    /**
     * 获取实时实例
     *
     * @param stream 流
     * @param clazz  clazz
     * @return {@link T}
     */
    public static <T extends MediaReq> T getLiveInstance(String stream, Class<T> clazz) {
        return getInstance(MediaEnum.App.live.name(), stream, clazz);
    }
    /**
     * 获得实例
     *
     * @param app    应用程序
     * @param stream 流
     * @return {@link MediaReq}
     */
    public static <T extends MediaReq> T getInstance(MediaEnum.App app, String stream, Class<T> clazz) {
        return getInstance(app.name(), stream, clazz);
    }

    /**
     * 获得实例
     *
     * @param app    应用程序
     * @param stream 流
     * @return {@link MediaReq}
     */
    @SneakyThrows
    public static <T extends MediaReq> T getInstance(String app, String stream, Class<T> clazz) {
        T t = clazz.newInstance();
        t.setSchema("rtsp");
        t.setVhost("__defaultVhost__");
        t.setApp(app);
        t.setStream(stream);
        return t;
    }
    /**
     * 获得实例
     *
     * @param stream 流
     * @return {@link MediaReq}
     */
    @SneakyThrows
    public static <T extends MediaReq> T getRtmpInstance( String stream, Class<T> clazz) {
        T t = clazz.newInstance();
        t.setSchema("rtmp");
        t.setVhost("__defaultVhost__");
        t.setApp("rtp");
        t.setStream(stream);
        return t;
    }

}
