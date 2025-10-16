package com.shdatalink.sip.server.media.bean.entity;

import com.shdatalink.sip.server.media.bean.entity.resp.MediaServerResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class DeleteRecordDirectory extends MediaServerResponse<String> {

    private String path;

}
