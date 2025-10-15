package com.shdatalink.sip.server.module.device.vo;

import lombok.Data;

import java.util.List;

@Data
public class DevicePreviewPresetVO {

    /**
     * 屏幕数量
     */
    private Integer screenCount;

    /**
     * 播放通道列表
     */
    private List<DevicePreviewPlayVO> screenList;
}
