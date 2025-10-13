package com.shdatalink.sip.service.module.user.vo;

import com.shdatalink.sip.service.common.dto.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserPageParam extends PageParam {
    private String fullName;
}
