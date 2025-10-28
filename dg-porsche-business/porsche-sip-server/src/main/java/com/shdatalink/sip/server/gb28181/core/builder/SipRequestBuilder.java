package com.shdatalink.sip.server.gb28181.core.builder;

import com.shdatalink.sip.server.gb28181.core.bean.model.base.GbDevice;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SipRequestBuilder {
    public static InviteRequest invite(GbDevice gbDevice) {
        return new InviteRequest(gbDevice);
    }
}
