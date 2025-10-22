package com.shdatalink.sip.server.app.device.vo;

import com.shdatalink.sip.server.common.dto.PageParamWithGet;
import jakarta.ws.rs.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AppDevicePageParam extends PageParamWithGet {
    /**
     * 在线状态 true-在线 false-离线 null-不筛选
     */
    @QueryParam("online")
    private Boolean online;

    /**
     * 设备名称或编码
     */
    @QueryParam("keyword")
    private String keyword;
}
