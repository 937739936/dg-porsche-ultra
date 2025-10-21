package com.shdatalink.sip.server.module.config.vo;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 录像配置
 */
@Data
@RegisterForReflection
public class VideoRecordConfig {
    /**
     * 录像保存路径
     */
    private String storePath;
    /**
     * 录像保存天数
     */
    private Integer storeDays;
    /**
     * 磁盘存储阈值
     */
    private BigDecimal diskStoreMax;
}
