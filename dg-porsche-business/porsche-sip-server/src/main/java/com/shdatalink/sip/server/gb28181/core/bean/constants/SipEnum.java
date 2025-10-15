package com.shdatalink.sip.server.gb28181.core.bean.constants;


import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * sip事件枚举
 *
 */
public class SipEnum {

    public enum Method {

        // BYE
        BYE,

        // 邀请
        INVITE,

        // 注册
        REGISTER,

        // 消息
        MESSAGE,

        // 订阅
        SUBSCRIBE,
        // 通知
        NOTIFY,
        //
        INFO,
        ;


        /**
         * 根据传入的方法匹配枚举值
         *
         * @param method 方法
         * @return {@link Method}
         */
        public static Optional<Method> resolve(String method) {
            return Stream.of(Method.values())
                         .filter(item -> StringUtils.equalsIgnoreCase(item.name(), method))
                         .findFirst();
        }
    }


    /**
     * 命令类型
     */
    public enum Cmd {
        NONE,

        /**
         * 设备信息
         */
        DeviceInfo,


        /**
         * 设备能力集
         */
        Capability,

        /**
         * keepalive
         */
        Keepalive,

        /**
         * 报警
         */
        Alarm,

        /**
         * 媒体状态
         */
        MediaStatus,

        /**
         * 移动位置
         */
        MobilePosition,

        /**
         * 目录
         */
        Catalog,

        /**
         * 设备状态
         */
        DeviceStatus,

        /**
         * 记录信息
         */
        RecordInfo,

        /**
         * 设备控制
         */
        DeviceControl,

        /**
         * 控制
         */
        Control,

        /**
         * 广播
         */
        Broadcast,

        /**
         * 配置下载
         */
        ConfigDownload,

        /**
         * 设备配置
         */
        DeviceConfig,

        /**
         * 预设查询
         */
        PresetQuery,

        ;

        /**
         * 根据传入的方法匹配枚举值
         *
         * @param cmd cmd
         * @return {@link Cmd}
         */
        public static Cmd resolve(String cmd) {
            return Stream.of(Cmd.values()).filter(item -> StringUtils.equalsIgnoreCase(item.name(), cmd)).findFirst().orElse(null);
        }

    }


}
