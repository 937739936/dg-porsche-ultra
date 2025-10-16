package com.shdatalink.sip.server.gb28181.core.builder;

import com.shdatalink.sip.server.gb28181.core.bean.model.base.GbDevice;

import javax.sip.SipException;
import javax.sip.message.Request;

public interface GBRequest {

    static InviteRequest invite(GbDevice gbDevice) {
        return new InviteRequest(gbDevice);
    }

    static ByeRequest bye(GbDevice gbDevice) {
        return new ByeRequest(gbDevice);
    }

    static MessageRequest message(GbDevice gbDevice) {
        return new MessageRequest(gbDevice);
    }

    default GBRequest execute() {
        return null;
    }
    default GBRequest execute(Object content) {
        return null;
    }

    default <T> T get() throws SipException {
        return null;
    };

    Request getRequest();

    static SubscribeRequest subscribe(GbDevice gbDevice) {
        return new SubscribeRequest(gbDevice);
    }

}
