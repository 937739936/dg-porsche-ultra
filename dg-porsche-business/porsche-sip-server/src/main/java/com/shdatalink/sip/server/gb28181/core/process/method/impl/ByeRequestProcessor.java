package com.shdatalink.sip.server.gb28181.core.process.method.impl;

import com.shdatalink.sip.server.gb28181.core.bean.annotations.SipEvent;
import com.shdatalink.sip.server.gb28181.core.bean.constants.SipEnum;
import com.shdatalink.sip.server.gb28181.core.builder.ResponseBuilder;
import com.shdatalink.sip.server.gb28181.core.process.method.AbstractSipRequestProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;

@Slf4j
@SipEvent(SipEnum.Method.BYE)
@ApplicationScoped
public class ByeRequestProcessor  extends AbstractSipRequestProcessor {
    @Override
    public void request(RequestEvent requestEvent) {
        ResponseBuilder.of(requestEvent).ok().execute();
    }

    @Override
    public void response(ResponseEvent responseEvent) {
    }
}
