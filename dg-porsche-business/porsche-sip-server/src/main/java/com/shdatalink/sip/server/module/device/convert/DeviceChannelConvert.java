package com.shdatalink.sip.server.module.device.convert;

import com.shdatalink.framework.common.config.QuarkusMappingConfig;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import com.shdatalink.sip.server.module.device.vo.DeviceChannelPage;
import org.mapstruct.Mapper;

@Mapper(config = QuarkusMappingConfig.class)
public interface DeviceChannelConvert {
    DeviceChannelPage toDeviceChannelPage(DeviceChannel deviceChannel);
}
