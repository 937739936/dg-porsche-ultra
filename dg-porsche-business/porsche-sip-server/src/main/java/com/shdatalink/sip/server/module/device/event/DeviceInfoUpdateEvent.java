package com.shdatalink.sip.server.module.device.event;

import com.shdatalink.sip.server.module.device.entity.Device;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceInfoUpdateEvent {
    private Device device;
}
