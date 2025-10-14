package com.shdatalink.sip.service.module.common.enums;

import com.shdatalink.framework.common.model.IDict;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnableDisableEnum implements IDict<String> {
    /**
     * 启用、禁用
     */
    ENABLE("ENABLE","启用"),
    DISABLE("DISABLE","禁用"),
    ;
    private final String code;
    private final String text;

}
