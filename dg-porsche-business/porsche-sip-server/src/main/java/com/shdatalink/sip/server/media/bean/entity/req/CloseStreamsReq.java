package com.shdatalink.sip.server.media.bean.entity.req;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CloseStreamsReq extends MediaReq {

    private int force;

    public CloseStreamsReq(String stream) {
        this.stream = stream;
    }

    public CloseStreamsReq(String stream, int force) {
        this.stream = stream;
        this.force = force;
    }
}