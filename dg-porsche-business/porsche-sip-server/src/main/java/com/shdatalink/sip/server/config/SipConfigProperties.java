package com.shdatalink.sip.server.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import lombok.Data;


@Data
@ConfigMapping(prefix = "sip")
public class SipConfigProperties {
    /**
     * 是否启用SIP日志，ERROR, INFO, WARNING, OFF, DEBUG, TRACE 默认是：OFF
     */
    @WithDefault("INFO")
    private String logs = "INFO";

    /**
     * 超时时间 【单位秒】
     */
    private Integer timeout;

    /**
     * 服务器
     */
    private SipServerConf server;

    /**
     * 全局订阅
     */
    private SipSubscribe subscribe;

    /**
     * 配置信息
     */
    private SipMapConf map;


    private ZlmMedia media;

    /**
     * sip订阅
     *
     */
    @Data
    public static class SipSubscribe {

        /**
         * 全局-订阅目录
         */
        private Boolean catalog = Boolean.FALSE;
        /**
         * 全局-订阅报警
         */
        private Boolean alarm = Boolean.FALSE;
        /**
         * 全局-订阅位置
         */
        private Boolean location = Boolean.FALSE;

    }


    @Data
    public static class SipMapConf {

        /**
         * 启用
         */
        private Boolean enable = false;

        /**
         * 地图中心
         */
        private String center = "117.17159813310452,31.83907609118903";


    }

    @Data
    public static class SipServerConf {
        /**
         * SIP服务器ID
         */
        private String id;

        /**
         * SIP服务器域 (domain宜采用ID统一编码的前十位编码)
         */
        private String domain;

        /**
         * SIP服务器地址，一般是本机IP
         * 不填写默认为：`0.0.0.0`
         */
        private String ip;


        /**
         * 公网ip
         */
        private String wanIp;

        /**
         * SIP服务器端口
         */
        private Integer port = 5060;

    }

    @Data
    public static class ZlmMedia {

        /**
         * IP
         */
        private String ip;

        /**
         * 端口
         */
        private int port;

        /**
         * 媒体id
         */
        private String mediaId;

        /**
         * 秘密
         */
        private String secret;

        /**
         * Mp4最大秒
         */
        private int mp4MaxSecond = 600;

        /**
         * 录制文件地址,默认 `./www下`
         */
        private String recordPath;
        /**
         * 快照文件地址
         */
        private String snapPath;

        /**
         * 录制倍率
         */
        private Double recordSpeed = 1.0;

        /**
         * 获得主机地址
         *
         * @return {@link String}
         */
        public String getHost() {
            return String.format("http://%s:%d", this.ip, this.port);
        }
    }
}
