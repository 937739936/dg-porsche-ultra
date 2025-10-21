package com.shdatalink.sip.server.module.config.enums;

import com.shdatalink.sip.server.module.config.vo.SystemBaseConfig;
import com.shdatalink.sip.server.module.config.vo.VideoRecordConfig;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ConfigTypesEnum {
    /**
     * 录像配置
     */
    VideoRecord(VideoRecordConfig.class),
    /**
     * 系统基础配置
     */
    SystemBase(SystemBaseConfig.class),
    ;
    private final Class<?> clazz;

    @SuppressWarnings("unchecked")
    public <T> Class<T> getClazz() {
        return (Class<T>) clazz;
    }
}
