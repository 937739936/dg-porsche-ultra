package com.shdatalink.sip.service.module.common.enums;

import com.shdatalink.framework.common.model.IDict;
import lombok.Getter;

@Getter
public enum OperateLogTypeEnum implements IDict<String> {
    /**
     * 登录、查询、新增、修改、删除
     */
    LOGIN("LOGIN", "登录"),
    QUERY("QUERY", "查询"),
    ADD("ADD", "新增"),
    UPDATE("UPDATE", "修改"),
    DELETE("DELETE", "删除");

    private final String code;
    private final String desc;
    OperateLogTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
        init(code, desc);
    }

}
