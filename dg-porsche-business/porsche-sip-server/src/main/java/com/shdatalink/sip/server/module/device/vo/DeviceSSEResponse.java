package com.shdatalink.sip.server.module.device.vo;

import com.shdatalink.sip.server.gb28181.core.bean.constants.SipEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeviceSSEResponse {
    public enum EventType {
        // 在线/离线状态
        Online,
        // 通信日志
        MessageLog,
        // 设备注册信息更新
        InfoUpdate,
        // 通道信息更新
        ChannelUpdate,
    }

    @Data
    public static class MessageLog {
        /**
         * 查询类型
         */
        private SipEnum.Cmd cmdType;
        /**
         * 时间
         */
        private LocalDateTime createTime;
        /**
         * 查询是否成功
         */
        private Boolean success;
        /**
         * 内容
         */
        private String content;
    }

    @Data
    public static class Online {
        /**
         * 设备id
         */
        private String deviceId;
        /**
         * 通道id
         */
        private String channelId;
        /**
         * 设备在线状态
         */
        private Boolean online;
    }
}
