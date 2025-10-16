package com.shdatalink.sip.server.media.hook.resp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HookResp {

    private Integer code;

    private String msg;

    public HookResp(Integer code) {
        this.code = code;
    }
}
