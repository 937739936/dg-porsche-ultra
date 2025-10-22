package com.shdatalink.sip.server.media.bean.entity.resp;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class RtpSenderListResult extends MediaServerResponse<List> {

    private int bytesSpeed;

    private int totalBytes;

}
