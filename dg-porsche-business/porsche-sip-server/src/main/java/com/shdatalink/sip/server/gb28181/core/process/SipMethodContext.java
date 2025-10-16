package com.shdatalink.sip.server.gb28181.core.process;

import com.shdatalink.sip.server.gb28181.core.bean.constants.SipEnum;
import com.shdatalink.sip.server.gb28181.core.process.method.AbstractSipRequestProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SipMethodContext {


    private static final Map<SipEnum.Method, AbstractSipRequestProcessor> REGISTER_MAP = new ConcurrentHashMap<>();


    public static void registerStrategy(SipEnum.Method sipMethod, AbstractSipRequestProcessor sipPrequestProcessor) {
        REGISTER_MAP.putIfAbsent(sipMethod, sipPrequestProcessor);
    }

    public static AbstractSipRequestProcessor execute(String method) {
        SipEnum.Method requestMethod = SipEnum.Method.resolve(method)
                .orElseThrow(() -> new NoSuchElementException("sip请求方法不支持,请联系管理员."));
        AbstractSipRequestProcessor processor = REGISTER_MAP.get(requestMethod);
        if (processor == null) {
            log.error("{} 处理失败", method);
        }
        return processor;
    }


}
