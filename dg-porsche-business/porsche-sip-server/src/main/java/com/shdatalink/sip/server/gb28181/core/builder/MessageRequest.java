package com.shdatalink.sip.server.gb28181.core.builder;

import com.shdatalink.sip.server.gb28181.core.bean.constants.SipConstant;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.GbDevice;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.Message;
import com.shdatalink.sip.server.gb28181.core.bean.model.device.message.query.QueryMessage;
import com.shdatalink.sip.server.utils.SipUtil;
import com.shdatalink.sip.server.utils.XmlUtil;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.sip.SipException;

@Slf4j
public class MessageRequest extends GBRequest {

    @Setter
    private Object content;
    private String key;

    public MessageRequest(GbDevice toDevice) {
        super(toDevice, SIPRequest.MESSAGE);
    }

    public MessageRequest execute(Object content) {
        this.content = content;
        byte[] xmlData = XmlUtil.toByteXml(content, SipConstant.CHARSET);
        createRequest()
                .setContent(SipConstant.XML, content)
                .send(false);
        return this;
    }

    public <T> T get() throws SipException {
        if (!(content instanceof QueryMessage queryMessage)) {
            return null;
        }
        if (key == null) {
            this.key = SipUtil.genSubscribeKey(queryMessage.getCmdType().name(), queryMessage.getDeviceId(), queryMessage.getSn());
        }
        FutureEvent<T> futureEvent = SipPublisher.subscribe(key).build();
        if (futureEvent.get().getCode() == Message.EnumState.OK) {
            return futureEvent.get().getData();
        } else {
            throw new SipException("message请求出错: cmdType=" + queryMessage.getCmdType().name() + "; code = " + futureEvent.get().getCode());
        }
    }

}
