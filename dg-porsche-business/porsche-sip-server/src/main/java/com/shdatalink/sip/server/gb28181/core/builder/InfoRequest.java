package com.shdatalink.sip.server.gb28181.core.builder;

import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.sip.server.gb28181.core.bean.constants.SipConstant;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.GbDevice;
import com.shdatalink.sip.server.utils.SipUtil;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.SneakyThrows;

import javax.sip.Dialog;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InfoRequest extends GBRequest {
    private float speed;
    private String streamId;
    private final GbDevice toDevice;

    private static final Map<String, Integer> cseq = new ConcurrentHashMap<>();

    public InfoRequest(GbDevice toDevice) {
        super(toDevice, SIPRequest.INFO);
        this.toDevice = toDevice;
    }

    public InfoRequest withStreamId(String streamId) {
        this.streamId = streamId;
        return this;
    }

    public InfoRequest withSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    @SneakyThrows
    public GBRequest execute() {
        Integer cseqNumber = cseq.compute(streamId, (k, v) -> v == null ? 1 : v + 1);
        List<String> msg = new ArrayList<>();
        msg.add("PLAY RTSP/1.0");
        msg.add("CSeq: " + cseqNumber);
        msg.add(String.format("Scale: %.2f", speed));

        Dialog dialog = DialogHolder.getDialog(streamId);
        if (dialog == null) {
            throw new BizException("会话不存在,不能调整倍速");
        }
        setRequest(dialog.createRequest(SIPRequest.INFO))
                .setSubject(String.format("%s:0,%s:0", toDevice.getChannelId(), sipConf.id()))
                .setContent(SipConstant.RTSP, String.join("\r\n", msg) + "\r\n")
                .setCSeqHeader(SipUtil.createCSeqHeader(DialogHolder.generateSeqNumber(streamId), SIPRequest.INFO))
                .send(false);
        return this;
    }
}
