package com.shdatalink.sip.server.common.validation;

import com.shdatalink.sip.server.common.validation.annotation.ValueList;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;


public class ValueListValidator implements ConstraintValidator<ValueList, Object> {

    private List<String> allowedValues;
    private String valueListString;
    private String messageTemplate;

    @Override
    public void initialize(ValueList constraintAnnotation) {
        this.allowedValues = Arrays.asList(constraintAnnotation.value());
        this.valueListString = String.join(", ", allowedValues);
        this.messageTemplate = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // 如果值为空，由 @NotNull 或 @NotBlank 处理
        if (value == null) {
            return true;
        }

        String currentValue = value.toString();

        // 检查值是否在允许的列表中
        boolean isValid = allowedValues.contains(currentValue);

        if (!isValid) {
            // 替换消息模板中的占位符
            String finalMessage = buildFinalMessage(currentValue);

            // 自定义错误消息
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(finalMessage).addConstraintViolation();
        }

        return isValid;
    }

    private String buildFinalMessage(String currentValue) {
        return messageTemplate
                .replace("{value}", valueListString)
                .replace("{currentValue}", currentValue)
                .replace("{allowedCount}", String.valueOf(allowedValues.size()));
    }
}
