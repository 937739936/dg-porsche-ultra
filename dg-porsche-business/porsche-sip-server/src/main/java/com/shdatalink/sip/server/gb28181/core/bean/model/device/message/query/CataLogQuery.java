package com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.enums.CmdType;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@RegisterForReflection
public class CataLogQuery extends QueryMessage {
    @Builder.Default
    private CmdType cmdType = CmdType.Catalog;
    /**
     * 增加设备的起始时间（可选）空表示不限
     */
    @JacksonXmlProperty(localName = "StartTime")
    private LocalDateTime startTime;

    /**
     * 增加设备的终止时间（可选）空表示到当前时间
     */
    @JacksonXmlProperty(localName = "EndTime")
    private LocalDateTime endTime;
}
