package com.shdatalink.sip.server.module.common.enums;

import com.shdatalink.framework.common.annotation.Anonymous;
import com.shdatalink.framework.common.model.IDict;
import com.shdatalink.sip.server.common.DictEnumCollector;
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

    // 静态块中注册当前枚举类
    static {
        DictEnumCollector.register(EnableDisableEnum.class);
    }
}
