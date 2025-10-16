package com.shdatalink.sip.server.gb28181.core.builder;

import com.shdatalink.framework.common.utils.SpringContextUtil;
import com.shdatalink.sip.server.config.SipConfigProperties;
import com.shdatalink.sip.server.gb28181.core.bean.constants.MediaStreamModeEnum;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.GbDevice;
import com.shdatalink.sip.server.gb28181.core.header.impl.XGBVerHeaderImpl;
import com.shdatalink.sip.server.util.SipUtil;
import gov.nist.javax.sip.header.Event;
import gov.nist.javax.sip.header.Expires;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.Request;
import java.util.List;

@Slf4j
public abstract class AbstractRequest implements GBRequest {

    @Setter
    public static int DEFAULT_MAX_FORWARD = 70;

    protected GbDevice toDevice;

    protected final SipConfigProperties.SipServerConf sipConf;
    protected CallIdHeader callIdHeader;
//    protected CSeqHeader cSeqHeader;
    protected FromHeader fromHeader;
    protected List<ViaHeader> viaHeaders;
    protected ToHeader toHeader;
    protected MediaStreamModeEnum streamMode;
    protected boolean transaction;

    protected String method;
    protected String subject;

    @Getter
    protected Request request;

    public AbstractRequest(GbDevice toDevice, String method) {
        this.toDevice = toDevice;
        this.streamMode = toDevice.getStreamMode();
        this.method = method;
        SipConfigProperties sipConfig = SpringContextUtil.getBean(SipConfigProperties.class);
        this.sipConf = sipConfig.getServer();
    }

    public AbstractRequest newSession() {
        return newSession(SipUtil.getNewCallId(toDevice.getTransport()), SipUtil.getNewFromTag());
    }

    /**
     * 新会话
     * @param callId
     * @param fromTag
     * @return
     */
    public AbstractRequest newSession(String callId, String fromTag) {
        return newSession(callId, fromTag, null);
    }

    /**
     * 新建会话，指定了toTag
     * @param callId
     * @param fromTag
     * @param toTag
     * @return
     */
    public AbstractRequest newSession(String callId, String fromTag, String toTag) {
        this.callIdHeader = SipUtil.createCallIdHeader(callId);
        this.viaHeaders = SipUtil.createViaHeaders(sipConf.getWanIp(), sipConf.getPort(), toDevice.getTransport(), SipUtil.generateViaTag());
        this.fromHeader = SipUtil.createFromHeader(SipUtil.createAddress(SipUtil.createSipURI(sipConf.getId(), sipConf.getDomain())), fromTag);
        SipURI toUri = SipUtil.createSipURI(toDevice.getChannelId(), SipUtil.createHostAddress(toDevice.getIp(), toDevice.getPort()));
        this.toHeader = SipUtil.createToHeader(SipUtil.createAddress(toUri), toTag);
        return this;
    }


    protected AbstractRequest transaction(boolean transaction) {
        this.transaction = transaction;
        return this;
    }

    private void check() {
        if (this.viaHeaders == null) {
            throw new RuntimeException("viaHeaders为空，确认是否开启了transaction");
        }
        if (this.callIdHeader == null || this.fromHeader == null || this.toHeader == null) {
            throw new RuntimeException("viaHeaders为空，确认是否开启了session");
        }
    }

    @SneakyThrows
    protected void request(String method) {
        check();
        SipURI toUri = SipUtil.createSipURI(toDevice.getChannelId(), SipUtil.createHostAddress(toDevice.getIp(), toDevice.getPort()));
        request = SipUtil.getMessageFactory().createRequest(
                toUri,
                method,
                this.callIdHeader,
                SipUtil.createCSeqHeader(SipUtil.getMessageSeq(), method),
                this.fromHeader,
                this.toHeader,
                this.viaHeaders,
                SipUtil.createMaxForwardsHeader(DEFAULT_MAX_FORWARD)
        );
        request.addHeader(XGBVerHeaderImpl.GB28181_2016);

        String local = SipUtil.createHostAddress(sipConf.getWanIp(), sipConf.getPort());
        Address localContact = SipUtil.createAddress(SipUtil.createSipURI(sipConf.getId(), local));
        request.addHeader(SipUtil.getHeaderFactory().createContactHeader(localContact));
        doRequest(request);
    }

    @SneakyThrows
    protected void request(String method, ContentTypeHeader contentType, Object content) {
        check();
        SipURI toUri = SipUtil.createSipURI(toDevice.getChannelId(), SipUtil.createHostAddress(toDevice.getIp(), toDevice.getPort()));
        request = SipUtil.getMessageFactory().createRequest(
                toUri,
                method,
                this.callIdHeader,
                SipUtil.createCSeqHeader(SipUtil.getMessageSeq(), method),
                this.fromHeader,
                this.toHeader,
                this.viaHeaders,
                SipUtil.createMaxForwardsHeader(DEFAULT_MAX_FORWARD),
                contentType,
                content
        );

        request.addHeader(XGBVerHeaderImpl.GB28181_2016);

        String local = SipUtil.createHostAddress(sipConf.getWanIp(), sipConf.getPort());
        Address localContact = SipUtil.createAddress(SipUtil.createSipURI(sipConf.getId(), local));
        request.addHeader(SipUtil.getHeaderFactory().createContactHeader(localContact));
        if (subject != null) {
            request.addHeader(SipUtil.getHeaderFactory().createSubjectHeader(subject));
        }
        doRequest(request);
    }

    @SneakyThrows
    protected void subscribeRequest(String method, ContentTypeHeader contentType, Object content, String eventType, int expiresTime) {
        check();
        SipURI toUri = SipUtil.createSipURI(toDevice.getChannelId(), SipUtil.createHostAddress(toDevice.getIp(), toDevice.getPort()));
        request = SipUtil.getMessageFactory().createRequest(
                toUri,
                method,
                this.callIdHeader,
                SipUtil.createCSeqHeader(SipUtil.getMessageSeq(), method),
                this.fromHeader,
                this.toHeader,
                this.viaHeaders,
                SipUtil.createMaxForwardsHeader(DEFAULT_MAX_FORWARD),
                contentType,
                content
        );
        request.addHeader(XGBVerHeaderImpl.GB28181_2016);
        Event event = new Event();
        event.setEventType(eventType);
        request.addHeader(event);
        Expires expires = new Expires();
        expires.setExpires(expiresTime);
        request.addHeader(expires);

        String local = SipUtil.createHostAddress(sipConf.getWanIp(), sipConf.getPort());
        Address localContact = SipUtil.createAddress(SipUtil.createSipURI(sipConf.getId(), local));
        request.addHeader(SipUtil.getHeaderFactory().createContactHeader(localContact));
        doRequest(request);
    }
    protected void doRequest(Request request) throws SipException {
        SipProvider sipProvider = SipUtil.getSipProvider(toDevice.getTransport());
        if (transaction) {
            ClientTransaction newClientTransaction = sipProvider.getNewClientTransaction(request);
            Dialog dialog = sipProvider.getNewDialog(newClientTransaction);
            dialog.sendRequest(newClientTransaction);
        } else {
            sipProvider.sendRequest(request);
        }
    }
}
