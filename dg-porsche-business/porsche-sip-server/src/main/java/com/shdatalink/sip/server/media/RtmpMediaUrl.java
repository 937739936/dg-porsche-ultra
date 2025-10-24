package com.shdatalink.sip.server.media;

import com.shdatalink.sip.server.gb28181.StreamFactory;
import com.shdatalink.sip.server.gb28181.core.bean.constants.InviteTypeEnum;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewPlayVO;
import jakarta.enterprise.context.ApplicationScoped;

import static com.shdatalink.sip.server.gb28181.core.bean.constants.SipConstant.SNAPSHOT_TOKEN;

@ApplicationScoped
public class RtmpMediaUrl extends AbstractMediaUrl {
    @Override
    public ProtocolTypeEnum type() {
        return ProtocolTypeEnum.RTMP;
    }

    @Override
    public DevicePreviewPlayVO play(Integer channelPrimaryId, int expire) {
        String stream = StreamFactory.streamId(InviteTypeEnum.Rtmp, channelPrimaryId.toString());
        String sign = mediaSignService.sign(stream, expire);
        return build(stream, sign, "");
    }

    @Override
    public String snapshot(Integer channelPrimaryId) {
        String stream = StreamFactory.streamId(InviteTypeEnum.Rtmp, channelPrimaryId.toString());
        return buildInner(stream, SNAPSHOT_TOKEN, "").getRtspUrl();
    }
}
