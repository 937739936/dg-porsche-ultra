package com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query;

import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.enums.CmdType;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class DeviceStatusQuery extends QueryMessage {
    @Builder.Default
    private CmdType cmdType = CmdType.DeviceStatus;
}
