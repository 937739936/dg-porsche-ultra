package com.shdatalink.sip.server.gb28181.core.bean.model.device.message.notify.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.DeviceBase;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JacksonXmlRootElement(localName = "Notify")
@RegisterForReflection
public class MediaStatus  extends DeviceBase {
    @JacksonXmlProperty(localName = "NotifyType")
    private Integer notifyType;

    public boolean historyDone() {
        return notifyType != null && notifyType == 121;
    }
}
