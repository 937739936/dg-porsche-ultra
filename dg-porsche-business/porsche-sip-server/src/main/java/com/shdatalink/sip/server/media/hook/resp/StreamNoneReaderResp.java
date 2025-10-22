package com.shdatalink.sip.server.media.hook.resp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StreamNoneReaderResp extends HookResp {

    /**
     * 是否关闭推流或拉流
     */
    private Boolean close;
}
