package com.shdatalink.sip.server.gb28181.core.builder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageStaging {
    private static final Map<String, Object> stagingMap = new ConcurrentHashMap<>();
    private static final Map<String, Long> timestampMap = new ConcurrentHashMap<>();
    public static <T> void put(String key, T value) {
        timestampMap.put(key, System.currentTimeMillis());
        stagingMap.put(key, value);

        // 清理已经过期的暂存内容
        for (String k : timestampMap.keySet()) {
            Long time = timestampMap.get(k);
            if (System.currentTimeMillis() - time > 5000) {
                stagingMap.remove(k);
            }
            timestampMap.remove(k);
        }
    }
    public static <T> T get(String key) {
        return (T) stagingMap.get(key);
    }
    public static void remove(String key) {
        stagingMap.remove(key);
    }
}
