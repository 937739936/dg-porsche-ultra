package com.shdatalink.framework.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class UnAuthorizedException extends RuntimeException {
    private int code;
    private String message;

    public UnAuthorizedException() {
        this.code = 401;
        this.message = "未授权";
    }
    public UnAuthorizedException(String message) {
        this.code = 401;
        this.message = message;
    }
}