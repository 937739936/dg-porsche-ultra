package com.shdatalink.sip.server.media.bean.entity.resp;

import lombok.Data;

@Data
public class MediaServerResponse<T> {

    private Integer code;

    private T data;

    private String msg;

    private String result;
}