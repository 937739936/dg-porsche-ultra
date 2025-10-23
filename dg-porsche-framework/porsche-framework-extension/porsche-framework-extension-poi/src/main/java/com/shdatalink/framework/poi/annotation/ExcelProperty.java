package com.shdatalink.framework.poi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelProperty {
    /**
     * 列名
     */
    String value();

    /**
     * 列顺序
     */
    int order() default 0;

    /**
     * 日期格式，对日期类型字段有效
     */
    String dateFormat() default "yyyy-MM-dd HH:mm:ss";

    /**
     * 单元格宽度，-1表示不设置
     */
    int width() default -1;
}
