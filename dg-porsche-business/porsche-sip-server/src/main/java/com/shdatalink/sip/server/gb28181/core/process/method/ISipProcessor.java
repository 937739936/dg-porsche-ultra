package com.shdatalink.sip.server.gb28181.core.process.method;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;

public interface ISipProcessor {
    void request(RequestEvent requestEvent);


    void response(ResponseEvent responseEvent);


}