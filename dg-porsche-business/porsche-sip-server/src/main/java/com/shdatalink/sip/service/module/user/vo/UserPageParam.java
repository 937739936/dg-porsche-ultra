package com.shdatalink.sip.service.module.user.vo;

import com.shdatalink.sip.service.common.dto.PageParam;
import com.shdatalink.sip.service.common.dto.PageParamWithGet;
import jakarta.ws.rs.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserPageParam extends PageParamWithGet {

    @QueryParam("fullName")
    private String fullName;
}
