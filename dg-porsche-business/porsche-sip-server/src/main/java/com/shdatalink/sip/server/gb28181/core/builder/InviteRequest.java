package com.shdatalink.sip.server.gb28181.core.builder;

import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.sip.server.gb28181.core.bean.constants.InviteTypeEnum;
import com.shdatalink.sip.server.gb28181.core.bean.constants.SipConstant;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.GbDevice;
import com.shdatalink.sip.server.gb28181.core.sdp.GB28181Description;
import com.shdatalink.sip.server.gb28181.core.sdp.GB28181SDPBuilder;
import gov.nist.javax.sip.message.SIPRequest;

import javax.sdp.Connection;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class InviteRequest extends AbstractRequest implements GBRequest {
    private String mediaIp;
    private Integer mediaPort;
    private String streamId;
    private InviteTypeEnum action;
    private LocalDateTime start;
    private LocalDateTime end;

    public InviteRequest(GbDevice toDevice) {
        super(toDevice, SIPRequest.INVITE);
        transaction(true);
        newSession();
    }

    public InviteRequest withStreamId(String streamId) {
        this.streamId = streamId;
        return this;
    }

    public InviteRequest withMediaAddress(String mediaIp, Integer port) {
        this.mediaIp = mediaIp;
        this.mediaPort = port;
        return this;
    }

    public InviteRequest play() {
        this.action = InviteTypeEnum.Play;
        return this;
    }

    public InviteRequest playBack(LocalDateTime start, LocalDateTime end) {
        this.action = InviteTypeEnum.Playback;
        this.start = start;
        this.end = end;
        return this;
    }

    public InviteRequest download(LocalDateTime start, LocalDateTime end) {
        this.action = InviteTypeEnum.Download;
        this.start = start;
        this.end = end;
        return this;
    }

    @Override
    public InviteRequest execute() {
        if (mediaIp == null) {
            request(SIPRequest.INVITE);
        } else {
            if (streamId == null) {
                streamId = toDevice.getChannelId();
            }
            if (action == null) {
                throw new BizException("请设置点播类型");
            }
            GB28181Description gbDescription;
            if (action == InviteTypeEnum.Play) {
                gbDescription = GB28181SDPBuilder.Receiver.play(
                        toDevice.getChannelId(),
                        toDevice.getChannelId(),
                        Connection.IP4,
                        mediaIp,
                        mediaPort,
                        streamId,
                        streamMode
                );
            } else if (action == InviteTypeEnum.Playback) {
                gbDescription = GB28181SDPBuilder.Receiver.playback(
                        toDevice.getChannelId(),
                        toDevice.getChannelId(),
                        Connection.IP4,
                        mediaIp,
                        mediaPort,
                        streamId,
                        streamMode,
                        Date.from(this.start.atZone(ZoneId.systemDefault()).toInstant()),
                        Date.from(this.end.atZone(ZoneId.systemDefault()).toInstant())
                );
            } else if (action == InviteTypeEnum.Download) {
                gbDescription = GB28181SDPBuilder.Receiver.download(
                        toDevice.getChannelId(),
                        toDevice.getChannelId(),
                        Connection.IP4,
                        mediaIp,
                        mediaPort,
                        streamId,
                        streamMode,
                        Date.from(this.start.atZone(ZoneId.systemDefault()).toInstant()),
                        Date.from(this.end.atZone(ZoneId.systemDefault()).toInstant()),
                        4
                );
            } else {
                throw new BizException("点播类型["+action+"]暂不支持");
            }
            subject = String.format("%s:%s,%s:0", toDevice.getChannelId(), streamId, sipConf.id());
            request(SIPRequest.INVITE, SipConstant.SDP, gbDescription);
        }
        return this;
    }
}
