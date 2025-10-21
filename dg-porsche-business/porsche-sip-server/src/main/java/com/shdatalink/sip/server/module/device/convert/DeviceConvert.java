package com.shdatalink.sip.server.module.device.convert;

import com.shdatalink.framework.common.config.QuarkusMappingConfig;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.vo.DeviceAddParam;
import com.shdatalink.sip.server.module.device.vo.DeviceImportVO;
import com.shdatalink.sip.server.module.device.vo.DevicePage;
import com.shdatalink.sip.server.module.user.entity.User;
import com.shdatalink.sip.server.module.user.vo.UserDetailVO;
import com.shdatalink.sip.server.module.user.vo.UserInfo;
import com.shdatalink.sip.server.module.user.vo.UserPage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(config = QuarkusMappingConfig.class)
public interface DeviceConvert {

    @Mappings({
            @Mapping(target = "manufacturer", ignore = true),
            @Mapping(target = "deviceType", ignore = true),
    })
    DeviceAddParam toDeviceAddParam(DeviceImportVO row);

    DevicePage toDevicePage(Device item);
}
