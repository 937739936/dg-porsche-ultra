package com.shdatalink.sip.server.module.device.event;

import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelUpdateEvent {
    private DeviceChannel channel;
    private Type type;

    public enum Type {
        // 新增
        New,
    }
}
