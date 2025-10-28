package com.shdatalink.sip.server.media.bean.entity.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class StartRecordReq extends RecordReq {
    /**
     * 自定义搜索路径，与startRecord方法中的customized_path一样，默认为配置文件的路径
     */
    @JsonProperty("customized_path")
    private String customizedPath;

    /**
     * mp4录像切片时间大小,单位秒，置0则采用配置项
     */
    @JsonProperty("max_second")
    private int maxSecond;
}
