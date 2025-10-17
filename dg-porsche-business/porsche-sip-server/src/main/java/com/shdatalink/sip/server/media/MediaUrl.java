package com.shdatalink.sip.server.media;

import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewPlayVO;

import java.time.LocalDateTime;

public interface MediaUrl {
    ProtocolTypeEnum type();

    /**
     * 获取直播url
     */
    DevicePreviewPlayVO play(Integer channelPrimaryId);

    /**
     * 获取回放url
     */
    DevicePreviewPlayVO playback(Integer channelPrimaryId, LocalDateTime start);

    /**
     * 获取截图url
     */
    String snapshot(Integer channelPrimaryId);
}
