package com.shdatalink.sip.service.module.config.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 录像配置
 */
@Data
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
