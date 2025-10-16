package com.shdatalink.sip.server.module.plan.vo;

import com.shdatalink.sip.server.common.dto.PageParam;
import lombok.Data;

@Data
public class VideoPlanPageParam extends PageParam {
    /**
     * 按名称搜索
     */
    private String name;
}
