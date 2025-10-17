package com.shdatalink.excel.model;

import lombok.Data;

import java.util.Map;

/**
 * @author 赵玮
 * @Description excel读取参数
 * @create /2022/3/21
 * 导入时组名和行头不能一样
 */
@Data
public class ExcelImportVo {
    /**
     * 标题行数,默认0
     */
    private int titleRows;
    /**
     * 上传的excel类映射
     */
    private Class<?> pojoClass;
    /**
     * 导出 占位符 替换动态表头
     */
    Map<String, Object> placeholderMap;
}
