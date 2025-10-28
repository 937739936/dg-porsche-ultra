package com.shdatalink.sip.server.gb28181.core.builder;

import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.GbDevice;
import com.shdatalink.sip.server.utils.SipUtil;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.SneakyThrows;

import javax.sip.Dialog;
import javax.sip.message.Request;

public class ByeRequest extends GBRequest {
    private Dialog dialog;
    private long seq;

    public ByeRequest(GbDevice toDevice) {
        super(toDevice, SIPRequest.BYE);
    }

    public ByeRequest withStreamId(String streamId) {
        Dialog dialog = DialogHolder.getDialog(streamId);
        if (dialog == null) {
            throw new BizException("未查询到会话:" + streamId);
        }
        this.dialog = dialog;
        this.seq = DialogHolder.generateSeqNumber(streamId);
        return this;
    }

    @SneakyThrows
    public ByeRequest execute() {
        Request request = dialog.createRequest(SIPRequest.BYE);
        dialog.terminateOnBye(true);
        request.setHeader(SipUtil.createCSeqHeader(seq, SIPRequest.BYE));
        setRequest(request).send(false);
        return this;
    }
}
