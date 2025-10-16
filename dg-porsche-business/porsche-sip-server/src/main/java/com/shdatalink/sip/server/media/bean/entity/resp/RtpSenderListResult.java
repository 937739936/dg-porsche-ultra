package com.shdatalink.sip.server.media.bean.entity.resp;

import lombok.Data;

import java.util.List;

@Data
public class RtpSenderListResult extends MediaServerResponse<List> {

    private int bytesSpeed;

    private int totalBytes;

}
