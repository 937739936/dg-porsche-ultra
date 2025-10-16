package com.shdatalink.sip.server.media.bean.entity.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author luna
 * @version 1.0
 * @date 2023/12/3
 * @description:
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeleteRecordDictionaryReq extends MediaReq {
    /**
     * 流的录像日期，格式为2020-02-01,如果不是完整的日期，那么是搜索录像文件夹列表，否则搜索对应日期下的mp4文件列表
     */
    private String period;

    /**
     * 自定义搜索路径，与startRecord方法中的customized_path一样，默认为配置文件的路径
     */
    @JsonProperty("customized_path")
    private String customizedPath;
    /**
     * 指定文件
     */
    private String name;
}
