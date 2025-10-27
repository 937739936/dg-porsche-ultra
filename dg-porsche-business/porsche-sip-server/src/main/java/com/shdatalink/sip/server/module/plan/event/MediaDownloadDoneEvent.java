package com.shdatalink.sip.server.module.plan.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class MediaDownloadDoneEvent {
    private String channelId;
    private String callId;
}
