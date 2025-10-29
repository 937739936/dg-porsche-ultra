package com.shdatalink.framework.excel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * 单个Sheet的配置信息封装类
 *
 * @author huyulong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SheetData {

    /**
     * Sheet序号（从0开始）
     */
    private Integer sheetNo;

    /**
     * Sheet名称
     */
    private String sheetName;

    /**
     * 数据实体类（需要添加EasyExcel注解）
     */
    private Class<?> clazz;

    /**
     * 当前Sheet的数据列表
     */
    private List<?> dataList;
}
