package com.shdatalink.sip.server.gb28181;

import com.shdatalink.sip.server.gb28181.core.bean.constants.InviteTypeEnum;
import com.shdatalink.sip.server.module.device.enums.ProtocolTypeEnum;

public class StreamFactory {
    public static String liveStreamId(ProtocolTypeEnum protocolType, String stream) {
        return switch (protocolType) {
            case GB28181 -> streamId(InviteTypeEnum.Play, stream);
            case PULL -> streamId(InviteTypeEnum.PullStream, stream);
            case RTMP -> streamId(InviteTypeEnum.Rtmp, stream);
        };
    }

    public static String streamId(InviteTypeEnum action, String stream) {
        return streamId(action, 0, stream);
    }

    public static String streamId(InviteTypeEnum action, Integer seq, String stream) {
        return action.getPrefix()+String.format("%02d%06d", seq, Integer.parseInt(stream));
    }

    public static Integer extractChannel(String stream) {
        return Integer.parseInt(stream.substring(4));
    }
    public static InviteTypeEnum extractType(String stream) {return InviteTypeEnum.getByPrefix(stream.substring(0,2));}
}
