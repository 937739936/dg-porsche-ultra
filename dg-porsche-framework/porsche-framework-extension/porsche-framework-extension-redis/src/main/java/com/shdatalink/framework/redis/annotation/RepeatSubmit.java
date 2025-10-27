package com.shdatalink.framework.redis.annotation;

import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.InterceptorBinding;
import jakarta.ws.rs.core.HttpHeaders;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * 自定义注解防止表单重复提交
 *
 * @author huyulong
 */
@Inherited
@InterceptorBinding
@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface RepeatSubmit {

    /**
     * 间隔时间(ms)，小于此时间视为重复提交
     */
    @Nonbinding
    int interval() default 1000;

    /**
     * 时间单位 默认秒
     */
    @Nonbinding
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 提示消息
     */
    @Nonbinding
    String message() default "不允许重复提交，请稍后再试";

    /**
     * token名称
     */
    @Nonbinding
    String tokenName() default HttpHeaders.AUTHORIZATION;
}
