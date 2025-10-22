package com.shdatalink.sip.server.module.device;

import com.shdatalink.framework.common.annotation.Anonymous;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.sip.server.gb28181.SipMessageTemplate;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.response.DeviceCatalog;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.service.DeviceService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("test/device")
@Anonymous
public class TestDeviceController {
    @Inject
    SipMessageTemplate sipMessageTemplate;

    @Inject
    DeviceService deviceService;

    @Path("catalog/{deviceId}")
    @GET
    public DeviceCatalog catalog(@PathParam("deviceId") String deviceId) {
        Device device = deviceService.getByDeviceId(deviceId).orElseThrow(() -> new BizException("设备不存在"));
        return sipMessageTemplate.getDeviceCatalog(device.toGbDevice());
    }
}
