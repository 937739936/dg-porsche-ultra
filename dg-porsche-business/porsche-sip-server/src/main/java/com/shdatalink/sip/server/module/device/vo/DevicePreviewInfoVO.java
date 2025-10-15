package com.shdatalink.sip.server.module.device.vo;

import com.shdatalink.sip.server.gb28181.core.bean.constants.TransportTypeEnum;
import com.shdatalink.sip.server.module.device.enums.PtzTypeEnum;
import com.shdatalink.sip.server.module.device.enums.SIPProtocolEnum;
import lombok.Data;

@Data
public class DevicePreviewInfoVO {
    /**
     * 流媒体传输协议
     */
    private TransportTypeEnum transportType;
    /**
     * 协议类型
     */
    private SIPProtocolEnum protocol;
    /**
     * 平台定义通道编码
     */
    private String platformChannel;
    /**
     * 设备通道编码
     */
    private String deviceChannel;
    private PtzTypeEnum ptzType;
    private boolean ptz;
    public boolean getPtz() {
        if (ptzType == null) return false;
        return ptzType == PtzTypeEnum.PTZCamera;
    }
}
