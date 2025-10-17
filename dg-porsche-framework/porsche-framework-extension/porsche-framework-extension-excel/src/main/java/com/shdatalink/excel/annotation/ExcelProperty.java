package com.shdatalink.excel.annotation;


import java.lang.annotation.*;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelProperty {

    /**
     * Excel表头名称
     */
    String name();

    /**
     * 列索引（从0开始）
     */
    int index();

    /**
     * 日期格式（如果字段是日期类型，指定格式如：yyyy-MM-dd）
     */
    String dateFormat() default "";

}
