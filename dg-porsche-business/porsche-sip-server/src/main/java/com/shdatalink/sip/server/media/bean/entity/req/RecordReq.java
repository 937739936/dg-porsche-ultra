package com.shdatalink.sip.server.media.bean.entity.req;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecordReq extends MediaReq {
    private Integer type;

    public RecordReq(String stream, Integer type) {
        super(stream);
        this.type = type;
    }
}
