package com.shdatalink.sip.server.gb28181.core.process.method;

import com.shdatalink.sip.server.gb28181.core.bean.annotations.SipEvent;
import com.shdatalink.sip.server.gb28181.core.process.SipMethodContext;

public abstract class AbstractSipRequestProcessor implements ISipProcessor {


    public AbstractSipRequestProcessor() {
        SipEvent sipEvent = this.getClass().getAnnotation(SipEvent.class);
        if (sipEvent != null) {
            SipMethodContext.registerStrategy(sipEvent.value(), this);
        }
    }


}