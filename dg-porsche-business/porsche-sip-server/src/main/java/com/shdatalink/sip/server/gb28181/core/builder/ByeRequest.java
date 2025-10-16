package com.shdatalink.sip.server.gb28181.core.builder;

import com.shdatalink.sip.server.gb28181.core.bean.model.base.GbDevice;
import com.shdatalink.sip.server.utils.SipUtil;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.SneakyThrows;

import javax.sip.Dialog;

public class ByeRequest extends AbstractRequest implements GBRequest {
    private String streamId;
    public ByeRequest(GbDevice toDevice) {
        super(toDevice, SIPRequest.BYE);
        newSession();
    }

    public ByeRequest withStreamId(String streamId) {
        this.streamId = streamId;
        return this;
    }

    @Override
    @SneakyThrows
    public ByeRequest execute() {
        Dialog dialog = DialogHolder.getDialog(streamId);
        if (dialog == null) {
            return null;
        }
        request = dialog.createRequest(SIPRequest.BYE);
        dialog.terminateOnBye(true);
        request.setHeader(SipUtil.createCSeqHeader(DialogHolder.generateSeqNumber(streamId), SIPRequest.BYE));
        doRequest(request);

        return this;
    }
}
