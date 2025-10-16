package com.shdatalink.sip.server.gb28181.core.process.method.impl;

import com.shdatalink.sip.server.gb28181.core.bean.annotations.SipEvent;
import com.shdatalink.sip.server.gb28181.core.bean.constants.SipEnum;
import com.shdatalink.sip.server.gb28181.core.process.method.AbstractSipRequestProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;

@Slf4j
@SipEvent(SipEnum.Method.INFO)
@Component
public class InfoRequestProcessor extends AbstractSipRequestProcessor {
    @Override
    public void request(RequestEvent requestEvent) {
    }

    @Override
    public void response(ResponseEvent responseEvent) {
    }
}
