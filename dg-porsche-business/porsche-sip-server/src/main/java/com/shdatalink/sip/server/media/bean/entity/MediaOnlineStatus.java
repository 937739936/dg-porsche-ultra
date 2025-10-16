package com.shdatalink.sip.server.media.bean.entity;

import com.shdatalink.sip.server.media.bean.entity.resp.MediaServerResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author luna
 * @version 1.0
 * @date 2023/12/2
 * @description:
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MediaOnlineStatus extends MediaServerResponse<String> {
    private String online;

}
