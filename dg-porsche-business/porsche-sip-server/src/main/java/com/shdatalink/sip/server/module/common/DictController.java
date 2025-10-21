package com.shdatalink.sip.server.module.common;

import com.shdatalink.framework.common.model.IDict;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.*;

/**
 * 通用接口/字典
 */
@Path("admin/dict")
public class DictController {

    private final static Map<String, Map<String, String>> dict = new HashMap<>();

    void onStart(@Observes StartupEvent ev) {
        Reflections reflections = new Reflections("com.shdatalink.sip", new SubTypesScanner(false));
        Set<Class<? extends IDict>> iDictImplementers = reflections.getSubTypesOf(IDict.class);
        for (Class<? extends IDict> implementer : iDictImplementers) {
            // 过滤：只保留是枚举的类（使用 isEnum() 检查）
            if (implementer.isEnum()) {
                // 类型转换（安全，因为已确认是 Enum）
                IDict[] constants = implementer.getEnumConstants();
                if (constants == null) {
                    continue;
                }
                Map<String, String> map = new LinkedHashMap<>();
                for (IDict constant : constants) {
                    map.put(constant.getCode().toString(), constant.getText());
                }
                dict.put(implementer.getSimpleName(), map);
            }
        }
    }

    /**
     * 查询所有字典
     */
    @GET
    @Path("all")
    public Map<String, Map<String, String>> all() {
        return dict;
    }


}
