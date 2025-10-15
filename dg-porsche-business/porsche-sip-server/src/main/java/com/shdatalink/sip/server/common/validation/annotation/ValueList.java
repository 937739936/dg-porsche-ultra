package com.shdatalink.sip.server.common.validation.annotation;


import com.shdatalink.sip.server.common.validation.ValueListValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValueListValidator.class)
public @interface ValueList {
    String[] value();
    String message() default "只允许传入 {value}，当前值: '{currentValue}'";
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
