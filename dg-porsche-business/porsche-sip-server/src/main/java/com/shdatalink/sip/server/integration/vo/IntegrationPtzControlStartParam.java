package com.shdatalink.sip.server.integration.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class IntegrationPtzControlStartParam extends IntegrationPtzControlParam {

    /**
     * 操作序列，按住不放控制模式下必须
     */
    private String serialNo;
}
