package com.shdatalink.sip.server.module.user.vo;

import com.shdatalink.sip.server.common.dto.PageParamWithGet;
import jakarta.ws.rs.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserPageParam extends PageParamWithGet {

    @QueryParam("fullName")
    private String fullName;
}
