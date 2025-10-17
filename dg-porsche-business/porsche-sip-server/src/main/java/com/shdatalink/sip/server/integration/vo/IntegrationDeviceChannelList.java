package com.shdatalink.sip.server.integration.vo;

import com.shdatalink.sip.server.module.device.enums.PtzTypeEnum;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewPlayVO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IntegrationDeviceChannelList {
    /**
     * 通道id
     */
    private String channelId;
    /**
     * 设备名称
     */
    private String name;
    /**
     * 设备id
     */
    private String deviceId;
    /**
     * 是否在线
     */
    private Boolean online;
    /**
     * 离线时间
     */
    private LocalDateTime leaveTime;
    /**
     * 云台类型
     */
    private PtzTypeEnum ptzType;
    /**
     * 能否控制
     */
    private Boolean ptz;
    public Boolean getPtz() {
        return ptzType == PtzTypeEnum.PTZCamera;
    }

    /**
     * 播放链接
     */
    private DevicePreviewPlayVO playUrl;
}
