package com.shdatalink.sip.server.module.pushstream.convert;

import com.shdatalink.framework.common.config.QuarkusMappingConfig;
import com.shdatalink.sip.server.media.hook.req.PlayReq;
import com.shdatalink.sip.server.module.pushstream.dto.MediaViewerDTO;
import org.mapstruct.Mapper;

@Mapper(config = QuarkusMappingConfig.class)
public interface PushStreamConvert {
    MediaViewerDTO convert(PlayReq source);
}
