package com.shdatalink.sip.server.module.plan.vo;

import com.shdatalink.sip.server.common.dto.PageParam;
import com.shdatalink.sip.server.common.dto.PageParamWithGet;
import lombok.Data;

@Data
public class VideoPlanPageParam extends PageParamWithGet {
    /**
     * 按名称搜索
     */
    private String name;
}
