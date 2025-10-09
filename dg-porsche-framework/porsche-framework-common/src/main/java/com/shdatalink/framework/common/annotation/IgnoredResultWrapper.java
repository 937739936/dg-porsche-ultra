package com.shdatalink.framework.common.annotation;


import java.lang.annotation.*;

/**
 * 忽略统一返回体
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
public @interface IgnoredResultWrapper {
}
