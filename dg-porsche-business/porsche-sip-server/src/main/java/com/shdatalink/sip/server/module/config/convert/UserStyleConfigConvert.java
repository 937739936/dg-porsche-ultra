package com.shdatalink.sip.server.module.config.convert;

import com.shdatalink.framework.common.config.QuarkusMappingConfig;
import com.shdatalink.sip.server.module.config.entity.UserStyleConfig;
import com.shdatalink.sip.server.module.config.vo.UserStyleConfigSaveReq;
import com.shdatalink.sip.server.module.config.vo.UserStyleConfigVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(config = QuarkusMappingConfig.class)
public interface UserStyleConfigConvert {

    void updateEntity(UserStyleConfigSaveReq saveReq, @MappingTarget UserStyleConfig entity);

    UserStyleConfigVO toConfigVO(UserStyleConfig config);

}
