package com.shdatalink.sip.server.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import lombok.Data;


@ConfigMapping(prefix = "sip")
public interface SipConfigProperties {
    /**
     * 是否启用SIP日志，ERROR, INFO, WARNING, OFF, DEBUG, TRACE 默认是：OFF
     */
    @WithDefault("INFO")
    String logs();

    /**
     * 超时时间 【单位秒】
     */
    Integer timeout();

    /**
     * 服务器
     */
    SipServerConf server();

    /**
     * 全局订阅
     */
    SipSubscribe subscribe();

    /**
     * 配置信息
     */
    SipMapConf map();


    ZlmMedia media();

    /**
     * sip订阅
     *
     */
    interface SipSubscribe {

        /**
         * 全局-订阅目录
         */
        @WithDefault("false")
        Boolean catalog();
        /**
         * 全局-订阅报警
         */
        @WithDefault("false")
        Boolean alarm();
        /**
         * 全局-订阅位置
         */
        @WithDefault("false")
        Boolean location();

    }


    interface SipMapConf {

        /**
         * 启用
         */
        @WithDefault("false")
        Boolean enable();

        /**
         * 地图中心
         */
        @WithDefault("117.17159813310452,31.83907609118903")
        String center();

    }

    interface SipServerConf {
        /**
         * SIP服务器ID
         */
        String id();

        /**
         * SIP服务器域 (domain宜采用ID统一编码的前十位编码)
         */
        String domain();

        /**
         * SIP服务器地址，一般是本机IP
         * 不填写默认为：`0.0.0.0`
         */
        @WithDefault("0.0.0.0")
        String ip();


        /**
         * 公网ip
         */
        String wanIp();

        /**
         * SIP服务器端口
         */
        @WithDefault("5060")
        Integer port();

    }

    interface ZlmMedia {

        /**
         * IP
         */
        String ip();

        /**
         * 端口
         */
        int port();

        /**
         * 媒体id
         */
        String id();

        /**
         * 秘密
         */
        String secret();

        /**
         * Mp4最大秒
         */
        @WithDefault("600")
        int mp4MaxSecond();

        /**
         * 录制文件地址,默认 `./www下`
         */
        @WithDefault("./www")
        String recordPath();

        /**
         * 快照文件地址
         */
        String snapPath();

        /**
         * 录制倍率
         */
        @WithDefault("1.0")
        Double recordSpeed();

    }
}
