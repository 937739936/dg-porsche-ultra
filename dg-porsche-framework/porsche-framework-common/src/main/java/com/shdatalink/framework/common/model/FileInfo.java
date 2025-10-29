package com.shdatalink.framework.common.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件信息
 */
@Data
@Builder
public class FileInfo {

    /**
     * 文件名
     */
    private String name;

    /**
     * 文件后缀名
     */
    private String suffix;

    /**
     * 文件大小
     */
    private long size;

    /**
     * 文件路径
     */
    private String path;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;
}
