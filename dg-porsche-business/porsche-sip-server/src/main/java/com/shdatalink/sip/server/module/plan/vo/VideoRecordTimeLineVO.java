package com.shdatalink.sip.server.module.plan.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VideoRecordTimeLineVO {
    /**
     * 录像开始时间，毫秒
     */
    private Long startTime;
    /**
     * 录像时长毫秒
     */
    private Integer duration;
}
