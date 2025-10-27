package com.shdatalink.sip.server.gb28181.core.builder;

import com.shdatalink.framework.common.utils.QuarkusUtil;
import com.shdatalink.sip.server.config.SipConfigProperties;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.GbDevice;
import com.shdatalink.sip.server.gb28181.core.header.impl.XGBVerHeaderImpl;
import com.shdatalink.sip.server.utils.SipUtil;
import gov.nist.javax.sip.header.Event;
import gov.nist.javax.sip.header.Expires;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.sip.*;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.Request;
import java.util.List;

@Slf4j
public class GBRequest {

    @Setter
    public static int DEFAULT_MAX_FORWARD = 70;

    protected final SipConfigProperties.SipServerConf sipConf;

    private final String method;
    private GbDevice toDevice;

    @Getter
    private Request request;

    public static ByeRequest bye(GbDevice toDevice) {
        return new ByeRequest(toDevice);
    }

    public static InviteRequest invite(GbDevice toDevice) {
        return new InviteRequest(toDevice);
    }

    public static InfoRequest info(GbDevice toDevice) {
        return new InfoRequest(toDevice);
    }

    public static MessageRequest message(GbDevice toDevice) {
        return new MessageRequest(toDevice);
    }

    public static SubscribeRequest subscribe(GbDevice toDevice) {
        return new SubscribeRequest(toDevice);
    }

    public GBRequest(GbDevice gbDevice, String method) {
        this.toDevice = gbDevice;
        this.method = method;
        SipConfigProperties sipConfig = QuarkusUtil.getBean(SipConfigProperties.class);
        this.sipConf = sipConfig.server();
    }

    public GBRequest setDevice(GbDevice gbDevice) {
        this.toDevice = gbDevice;
        return this;
    }

    public GBRequest setRequest(Request request) {
        this.request = request;
        return this;
    }

    public GBRequest createRequest() {
        return createRequest(SipUtil.getNewCallId(toDevice.getTransport()), SipUtil.getNewFromTag());
    }

    public GBRequest createRequest(String callId, String fromTag) {
        return createRequest(callId, fromTag, null);
    }

    @SneakyThrows
    public GBRequest createRequest(String callId, String fromTag, String toTag) {
        CallIdHeader callIdHeader = SipUtil.createCallIdHeader(callId);
        List<ViaHeader> viaHeaders = SipUtil.createViaHeaders(sipConf.wanIp(), sipConf.port(), toDevice.getTransport(), SipUtil.generateViaTag());
        FromHeader fromHeader = SipUtil.createFromHeader(SipUtil.createAddress(SipUtil.createSipURI(sipConf.id(), sipConf.domain())), fromTag);
        SipURI toUri = SipUtil.createSipURI(toDevice.getChannelId(), SipUtil.createHostAddress(toDevice.getIp(), toDevice.getPort()));
        ToHeader toHeader = SipUtil.createToHeader(SipUtil.createAddress(toUri), toTag);
        this.request = SipUtil.getMessageFactory().createRequest(
                toUri,
                method,
                callIdHeader,
                SipUtil.createCSeqHeader(SipUtil.getMessageSeq(), method),
                fromHeader,
                toHeader,
                viaHeaders,
                SipUtil.createMaxForwardsHeader(DEFAULT_MAX_FORWARD)
        );
        this.request.addHeader(XGBVerHeaderImpl.GB28181_2016);

        String local = SipUtil.createHostAddress(sipConf.wanIp(), sipConf.port());
        Address localContact = SipUtil.createAddress(SipUtil.createSipURI(sipConf.id(), local));
        this.request.addHeader(SipUtil.getHeaderFactory().createContactHeader(localContact));
        return this;
    }

    @SneakyThrows
    public GBRequest setContent(ContentTypeHeader contentTypeHeader, Object content) {
        this.request.setContent(content, contentTypeHeader);
        return this;
    }

    @SneakyThrows
    public GBRequest setEventTypeHeader(String eventType) {
        Event event = new Event();
        event.setEventType(eventType);
        request.addHeader(event);
        return this;
    }

    @SneakyThrows
    public GBRequest setExpiresTimeHeader(int expiresTime) {
        Expires expires = new Expires();
        expires.setExpires(expiresTime);
        request.addHeader(expires);
        return this;
    }

    @SneakyThrows
    public GBRequest setSubject(String subject) {
        request.addHeader(SipUtil.getHeaderFactory().createSubjectHeader(subject));
        return this;
    }

    @SneakyThrows
    public GBRequest send(boolean transaction) {
        SipProvider sipProvider = SipUtil.getSipProvider(this.toDevice.getTransport());
        if (transaction) {
            ClientTransaction newClientTransaction = sipProvider.getNewClientTransaction(request);
            Dialog dialog = sipProvider.getNewDialog(newClientTransaction);
            dialog.sendRequest(newClientTransaction);
        } else {
            sipProvider.sendRequest(request);
        }
        return this;
    }

    public GBRequest setCSeqHeader(CSeqHeader cSeqHeader) {
        this.request.setHeader(cSeqHeader);
        return this;
    }
}
