package com.shdatalink.sip.server.gb28181.core.builder;

import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.sip.server.gb28181.core.bean.constants.SipConstant;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.GbDevice;
import com.shdatalink.sip.server.util.SipUtil;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.SneakyThrows;

import javax.sip.Dialog;
import java.util.ArrayList;
import java.util.List;

public class InfoRequest extends AbstractRequest implements GBRequest {
    private float speed;
    private String streamId;
    public InfoRequest(GbDevice toDevice) {
        super(toDevice, SIPRequest.INFO);
        newSession();
    }

    public InfoRequest withStreamId(String streamId) {
        this.streamId = streamId;
        return this;
    }

    public InfoRequest withSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    @Override
    @SneakyThrows
    public GBRequest execute() {
        List<String> msg = new ArrayList<>();
        msg.add("PLAY RTSP/1.0");
        msg.add("CSeq: 1.0");
        msg.add(String.format("Scale: %.2f", speed));

        subject = String.format("%s:0,%s:0", toDevice.getChannelId(), sipConf.getId());

        Dialog dialog = DialogHolder.getDialog(streamId);
        if (dialog == null) {
            throw new BizException("会话不存在,不能调整倍速");
        }
        request = dialog.createRequest(SIPRequest.INFO);
        request.setContent(String.join("\r\n", msg)+"\r\n", SipConstant.RTSP);
        request.addHeader(SipUtil.getHeaderFactory().createSubjectHeader(subject));
        request.setHeader(SipUtil.createCSeqHeader(DialogHolder.generateSeqNumber(streamId), SIPRequest.INFO));
        doRequest(request);
        return this;
    }
}
