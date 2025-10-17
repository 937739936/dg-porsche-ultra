package com.shdatalink.sip.server.gb28181.core.process.method.impl;

import com.shdatalink.sip.server.gb28181.core.bean.annotations.SipEvent;
import com.shdatalink.sip.server.gb28181.core.bean.constants.SipEnum;
import com.shdatalink.sip.server.gb28181.core.builder.DialogHolder;
import com.shdatalink.sip.server.gb28181.core.process.method.AbstractSipRequestProcessor;
import com.shdatalink.sip.server.gb28181.core.sdp.GB28181Description;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import javax.sip.Dialog;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.message.Request;
import javax.sip.message.Response;

@Startup
@Slf4j
@SipEvent(SipEnum.Method.INVITE)
@ApplicationScoped
public class InviteRequestProcessor extends AbstractSipRequestProcessor {
    @Override
    public void request(RequestEvent requestEvent) {
        log.info("InviteRequestProcessor request method invoke!!!");
    }

    @Override
    public void response(ResponseEvent responseEvent) {
        SIPResponse response = (SIPResponse) responseEvent.getResponse();
        int statusCode = response.getStatusCode();
        try {
            if (statusCode == Response.TRYING) {
            } else if (statusCode == Response.OK) {
                SIPRequest request = (SIPRequest) responseEvent.getClientTransaction().getRequest();
                GB28181Description sdp = (GB28181Description) request.getContent();
                String ssrc = sdp.getSsrcField().getSsrc();

                Dialog dialog = responseEvent.getClientTransaction().getDialog();
                Request ack = dialog.createAck(response.getCSeq().getSeqNumber());
                dialog.sendAck(ack);
                DialogHolder.putDialog(ssrc, dialog.getDialogId(), dialog);
            }
        } catch (Exception e) {
            log.error("「点播回复ACK」异常：", e);
        }
    }
}
