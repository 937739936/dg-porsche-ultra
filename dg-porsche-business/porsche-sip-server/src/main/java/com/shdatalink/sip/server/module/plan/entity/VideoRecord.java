package com.shdatalink.sip.server.module.plan.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_video_record")
public class VideoRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    private String deviceId;
    private String channelId;
    /**
     * 录像开始时间
     */
    private Long startTime;
    /**
     * 录像时长
     */
    private Integer duration;
    /**
     * mp4文件
     */
    private String filePath;
    /**
     * mp4文件目录
     */
    private String folder;
    /**
     * 录像日期
     */
    private LocalDate date;
    /**
     * 分辨率
     */
    private String resolution;
    /**
     * 码率
     */
    private Integer bandwidth;
    /**
     * 视频编码格式
     */
    private String videoCodec;
    /**
     * 音频编码格式
     */
    private String audioCodec;
    /**
     * 文件大小(原始mp4文件大小)
     */
    private Integer size;
}
