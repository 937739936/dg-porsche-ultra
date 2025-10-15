package com.shdatalink.sip.server.app.common;

import com.shdatalink.framework.common.model.IDict;
import com.shdatalink.sip.server.module.common.enums.EnableDisableEnum;
import com.shdatalink.sip.server.module.common.enums.OperateLogTypeEnum;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;


import java.util.*;

/**
 * APP/通用接口/字典
 */
@Path("app/dict")
public class AppDictController {

    private final static Map<String, Map<String, String>> dict = new HashMap<>();

    static {
        Set<Class<? extends IDict<?>>> classes = null;
        try {
            classes = scanEnums();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (Class<? extends IDict> enumClass : classes) {
            IDict[] constants = enumClass.getEnumConstants();
            if (constants == null) continue;
            Map<String, String> map = new LinkedHashMap<>();
            for (IDict constant : constants) {
                map.put(constant.getCode().toString(), constant.getText());
            }
            dict.put(enumClass.getSimpleName(), map);
        }

    }

    /**
     * 查询所有字典
     * @return
     */
    @GET
    @Path("all")
    public Map<String, Map<String, String>> all() {
        return dict;
    }

    public static Set<Class<? extends IDict<?>>> scanEnums() {
        Set<Class<? extends IDict<?>>> result = new HashSet<>();
        result.add(EnableDisableEnum.class);
        result.add(OperateLogTypeEnum.class);
        return result;
    }

}
