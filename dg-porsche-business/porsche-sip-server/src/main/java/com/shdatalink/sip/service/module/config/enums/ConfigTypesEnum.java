package com.shdatalink.sip.service.module.config.enums;

import com.shdatalink.sip.service.module.config.vo.VideoRecordConfig;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ConfigTypesEnum {
    /**
     * 录像配置
     */
    VideoRecord(VideoRecordConfig.class),

    ;
    private final Class<?> clazz;

    @SuppressWarnings("unchecked")
    public <T> Class<T> getClazz() {
        return (Class<T>) clazz;
    }
}
