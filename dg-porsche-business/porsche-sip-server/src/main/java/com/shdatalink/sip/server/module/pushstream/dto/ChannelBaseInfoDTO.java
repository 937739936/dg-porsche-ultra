package com.shdatalink.sip.server.module.pushstream.dto;

import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@RegisterForReflection
@Data
public class ChannelBaseInfoDTO {
    /**
     * 流(通道主键ID)
     */
    private String id;
    /**
     * 设备ID
     */
    private String deviceId;
    /**
     * 通道ID
     */
    private String channelId;
    /**
     * 协议类型
     */
    private ProtocolTypeEnum protocolType;
    /**
     * 是否在线
     */
    private Boolean online;
    /**
     * 流url
     */
    private String streamUrl;
}
