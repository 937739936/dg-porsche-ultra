package com.shdatalink.sip.service.module.user.convert;

import com.shdatalink.framework.common.config.QuarkusMappingConfig;
import com.shdatalink.sip.service.module.user.entity.User;
import com.shdatalink.sip.service.module.user.vo.UserDetailVO;
import com.shdatalink.sip.service.module.user.vo.UserInfo;
import com.shdatalink.sip.service.module.user.vo.UserPage;
import org.mapstruct.Mapper;


@Mapper(config = QuarkusMappingConfig.class)
public interface UserConvert {

    UserInfo toUserInfo(User user);

    UserPage toUserPage(User item);

    UserDetailVO toUserDetailVO(User user);
}
