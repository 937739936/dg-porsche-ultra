package com.shdatalink.excel.annotation;


import java.lang.annotation.*;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelProperty {
    /**
     * 导入 导出
     */
    String groupName() default "";

    /**
     * 导入 导出 时不能有annocation重名的 导出时的列名 导入时的列名
     */
    String name();

    /**
     * 导入 导出 展示列名的排序 导入时列名的排序
     */
    int orderNum() default 0;

    /**
     * 导出的时间格式,以这个是否为空来判断是否需要格式化日期
     */
    String exportFormat() default "";

    /**
     * 导出 文字后缀,如% 90 变成90%
     */
    String exportSuffix() default "";

    /**
     * 导出 {删除_1,未删除_0}
     */
    String[] exportReplace() default {};

    /**
     * 导出数据脱敏规则
     * 规则 1: 采用保留头和尾的方式,中间数据加星号
     * 如: 身份证  6_4 则保留 370101********1234
     * 手机号   3_4 则保留 131****1234
     * 规则 2: 采用确定隐藏字段的进行隐藏,优先保留头
     * 如: 姓名   1,3 表示最大隐藏3位,最小一位
     * 王 -->  *
     * 王三 --> 李*
     * 王全蛋  --> 张*蛋
     * 王张全蛋 --> 李**蛋
     * 尼古拉斯.赵四 -> 尼古***赵四
     * 规则3: 特殊符号后保留
     * 如: 邮箱    1~@ 表示只保留第一位和@之后的字段
     * aadasddas@qq.com -> a********@qq.com
     */
    String desensitizationRule() default "";

    /**
     * 导入是{1_删除,0_未删除}
     */
    String[] importReplace() default {};

    /**
     * 导入的时间格式,以这个是否为空来判断是否需要格式化日期
     */
    String importFormat() default "";

    /**
     * 导入 文字后缀,如% 90 变成90%
     */
    String importSuffix() default "";

    /**
     * 导入该属性是否是必须的
     */
    boolean importIsMust() default true;

    /**
     * 导出时在excel中每个列的宽 单位为字符，一个汉字=2个字符 如 以列名列内容中较合适的长度
     * 例如姓名列6 【姓名一般三个字】 性别列4【男女占1，但是列标题两个汉字】 限制1-255
     */
    int width() default 0;

}
