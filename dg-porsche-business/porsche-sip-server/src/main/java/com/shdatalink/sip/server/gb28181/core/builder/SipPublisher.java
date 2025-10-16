package com.shdatalink.sip.server.gb28181.core.builder;


import com.shdatalink.framework.common.utils.SpringContextUtil;
import com.shdatalink.sip.server.config.SipConfigProperties;
import com.shdatalink.sip.server.gb28181.core.bean.model.base.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class SipPublisher {
    public static final Map<String, CompletableFuture<Message<?>>> pendingRequestMap = new ConcurrentHashMap<>();

    public static SipPublisherBuilder subscribe(String key) {
        SipConfigProperties sipConfigProperties = SpringContextUtil.getBean(SipConfigProperties.class);
        return new SipPublisherBuilder(key, sipConfigProperties.getTimeout());
    }


    public static SipPublisherHandler handler(String key) {
        return new SipPublisherHandler(key);
    }

    public static void unSubscribe(String key) {
        pendingRequestMap.remove(key);
    }


}
