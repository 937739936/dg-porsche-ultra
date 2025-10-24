package com.shdatalink.sip.server.media;

import com.shdatalink.sip.server.gb28181.StreamFactory;
import com.shdatalink.sip.server.gb28181.core.bean.constants.InviteTypeEnum;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;
import com.shdatalink.sip.server.module.device.vo.DevicePreviewPlayVO;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PullMediaUrl extends AbstractMediaUrl {

    @Override
    public ProtocolTypeEnum type() {
        return ProtocolTypeEnum.PULL;
    }

    @Override
    public DevicePreviewPlayVO play(Integer channelPrimaryId, int expire) {
        String stream = StreamFactory.streamId(InviteTypeEnum.PullStream, channelPrimaryId.toString());
        String sign = mediaSignService.sign(stream, expire);
        return build(stream, sign, "");
    }
}
