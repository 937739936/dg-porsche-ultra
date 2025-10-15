package com.shdatalink.sip.server.gb28181.core.bean.model.device.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RemoteInfo {
    private String ip;
    private int port;
    private String transport;
    private String userId;

    public RemoteInfo(String ip, int port, String transport, String userId) {
        this.ip = ip;
        this.port = port;
        this.transport = transport;
        this.userId = userId;
    }

}
