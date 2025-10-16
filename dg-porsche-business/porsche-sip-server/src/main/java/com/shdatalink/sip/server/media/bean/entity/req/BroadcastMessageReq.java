package com.shdatalink.sip.server.media.bean.entity.req;

import lombok.Data;

@Data
public class BroadcastMessageReq extends MediaReq {
    /*
     * 广播的消息内容
     */
    private String msg;
}
