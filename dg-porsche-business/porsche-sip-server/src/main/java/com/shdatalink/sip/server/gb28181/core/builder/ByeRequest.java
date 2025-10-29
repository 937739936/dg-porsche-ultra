package com.shdatalink.sip.server.gb28181.core.builder;

import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.GbDevice;
import com.shdatalink.sip.server.utils.SipUtil;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.SneakyThrows;

import javax.sip.Dialog;
import javax.sip.message.Request;

public class ByeRequest extends GBRequest {
    private String streamId;

    public ByeRequest(GbDevice toDevice) {
        super(toDevice, SIPRequest.BYE);
    }

    public ByeRequest withStreamId(String streamId) {
        this.streamId = streamId;

        return this;
    }

    @SneakyThrows
    public ByeRequest execute() {
        Dialog dialog = DialogHolder.getDialog(streamId);
        if (dialog != null) {
            long seq = DialogHolder.generateSeqNumber(streamId);

            Request request = dialog.createRequest(SIPRequest.BYE);
            dialog.terminateOnBye(true);
            request.setHeader(SipUtil.createCSeqHeader(seq, SIPRequest.BYE));
            setRequest(request).send(false);
        }
        return this;
    }
}
