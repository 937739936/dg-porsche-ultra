package com.shdatalink.sip.server.integration.convert;

import com.shdatalink.framework.common.config.QuarkusMappingConfig;
import com.shdatalink.sip.server.integration.vo.IntegrationDevicePreviewPlayVO;
import com.shdatalink.sip.server.integration.vo.IntegrationPtzControlParam;
import com.shdatalink.sip.server.integration.vo.IntegrationPtzControlStopParam;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewPlayVO;
import com.shdatalink.sip.server.module.device.vo.PtzControlParam;
import com.shdatalink.sip.server.module.device.vo.PtzControlStopParam;
import org.mapstruct.Mapper;

@Mapper(config = QuarkusMappingConfig.class)
public interface IntegrationDeviceConvert {
    PtzControlParam toPtzControlParam(IntegrationPtzControlParam param);
    PtzControlStopParam toPtzControlParam(IntegrationPtzControlStopParam param);
    IntegrationDevicePreviewPlayVO toPlayVO(DevicePreviewPlayVO param);
}
