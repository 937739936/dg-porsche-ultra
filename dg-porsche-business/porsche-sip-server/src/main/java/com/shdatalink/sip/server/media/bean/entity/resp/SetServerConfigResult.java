package com.shdatalink.sip.server.media.bean.entity.resp;

import lombok.Data;


@Data
public class SetServerConfigResult {

    /**
     * 是否找到记录并关闭
     */
    private String changed;

    private int code;

}
