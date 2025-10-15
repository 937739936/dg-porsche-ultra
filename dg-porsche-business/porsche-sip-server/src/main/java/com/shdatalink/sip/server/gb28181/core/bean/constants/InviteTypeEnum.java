package com.shdatalink.sip.server.gb28181.core.bean.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum InviteTypeEnum {
    Play("01"),
    Playback("02"),
    Download("03"),
    PullStream("05"),
    Rtmp("06"),
    ;

    private final String prefix;

    public static InviteTypeEnum getByPrefix(String prefix) {
        for (InviteTypeEnum inviteTypeEnum : InviteTypeEnum.values()) {
            if (inviteTypeEnum.prefix.equals(prefix)) {
                return inviteTypeEnum;
            }
        }
        return null;
    }
}
