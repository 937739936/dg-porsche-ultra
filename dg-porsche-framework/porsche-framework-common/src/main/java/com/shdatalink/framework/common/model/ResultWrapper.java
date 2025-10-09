package com.shdatalink.framework.common.model;

import com.shdatalink.framework.common.exception.BaseResultCodeEnum;
import com.shdatalink.framework.common.exception.IResultCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * 统一返回对象
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
public class ResultWrapper<T> {
    private String code;
    private String message;
    private T data;


    public ResultWrapper(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultWrapper(IResultCode resultCode) {
        this(resultCode.getCode(), resultCode.getMessage());
    }

    public ResultWrapper(IResultCode resultCode, T data) {
        this(resultCode);
        this.data = data;
    }


    public static <T> ResultWrapper<T> success(T data) {
        return new ResultWrapper<>(BaseResultCodeEnum.SUCCESS, data);
    }

    public static <T> ResultWrapper<T> success() {
        return new ResultWrapper<>(BaseResultCodeEnum.SUCCESS);
    }


    public static ResultWrapper<Void> fail(IResultCode resultCode, String detailErrorMsg) {
        return new ResultWrapper<>(resultCode.getCode(), resultCode.getMessage() + "：" + detailErrorMsg);
    }

    public static <T> ResultWrapper<T> fail(IResultCode resultCode) {
        return new ResultWrapper<>(resultCode.getCode(), resultCode.getMessage());
    }

    public static <T> ResultWrapper<T> fail(String code, String message) {
        return new ResultWrapper<>(code, message);
    }


}
