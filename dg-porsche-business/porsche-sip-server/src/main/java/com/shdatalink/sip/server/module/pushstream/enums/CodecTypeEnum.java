package com.shdatalink.sip.server.module.pushstream.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CodecTypeEnum {
    /**
     * 编码类型。H264 = 0, H265 = 1, AAC = 2, G711A = 3, G711U = 4
     */
    H264(0, "H264"),
    H265(1, "H265"),
    AAC(2, "AAC"),
    G711A(3, "G711A"),
    G711U(4, "G711U"),
    ;
    private final int code;
    private final String text;

    public static String parseText(int code) {
        for (CodecTypeEnum value : CodecTypeEnum.values()) {
            if (value.getCode() == code) {
                return value.getText();
            }
        }
        return "";
    }
}
