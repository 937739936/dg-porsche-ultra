package com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify;

import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.enums.CmdType;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@RegisterForReflection
public class MobilePositionSubscribe extends SubscribeMessage {
    @Builder.Default
    private CmdType cmdType = CmdType.MobilePosition;
    /**
     * 移动设备位置信息上报时间间隔，单位：秒，默认值5（可选）
     */
    private Integer Interval;

}
