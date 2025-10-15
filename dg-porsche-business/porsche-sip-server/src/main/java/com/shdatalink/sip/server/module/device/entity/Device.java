package com.shdatalink.sip.server.module.device.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shdatalink.mysql.entity.BaseEntity;
import com.shdatalink.sip.server.gb28181.core.bean.constants.DeviceTypeEnum;
import com.shdatalink.sip.server.gb28181.core.bean.constants.MediaStreamModeEnum;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.GbDevice;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.Dto.RemoteInfo;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_device")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Device extends BaseEntity {

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 通道数量
     */
    private Integer channelCount;


    /**
     * 设备名称
     */
    private String name;
    /**
     * 协议类型
     */
    private ProtocolTypeEnum protocolType;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 本地sip端口
     */
    private Integer port;

    /**
     * 传输协议
     */
    private String transport;


    /**
     * 生产厂商
     */
    private String manufacturer;

    /**
     * 设备类型
     */
    private DeviceTypeEnum deviceType;

    /**
     * 注册密码
     */
    private String registerPassword;


    /**
     * 型号
     */
    private String model;

    /**
     * 固件版本
     */
    private String firmware;

    /**
     * 流模式
     */
    private MediaStreamModeEnum streamMode;

    /**
     * 拉流地址
     */
    private String streamUrl;
    /**
     * 是否启用
     */
    private Boolean enable;

    /**
     * 是否在线
     */
    private Boolean online;

    /**
     * 注册时间
     */
    private LocalDateTime registerTime;

    /**
     * 最新心跳时间
     */
    private LocalDateTime keepaliveTime;

    /**
     * 是否开启音频
     */
    private Boolean enableAudio;
    /**
     * 备注
     */
    private String remark;

    public static GbDevice toGbDevice(String channelId, RemoteInfo remoteInfo) {
        return new GbDevice(channelId, remoteInfo.getIp(), remoteInfo.getPort(), remoteInfo.getTransport(), null);
    }

    public GbDevice toGbDevice() {
        return new GbDevice(this.getDeviceId(), this.getIp(), this.getPort(), this.getTransport(), this.getStreamMode());
    }

    public GbDevice toGbDevice(String channelId) {
        return new GbDevice(channelId, this.getIp(), this.getPort(), this.getTransport(), this.getStreamMode());
    }
}

