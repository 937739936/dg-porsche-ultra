package com.shdatalink.excel.model;

import lombok.Data;

/**
 * @创建人 赵伟
 * @创建时间 2025/1/20
 * @描述 分组时属性数据
 */
@Data
public class ExcelGroupVo {

    /**
     * 不是组名称，而是具体行属性
     */
    private String name;
    /**
     * 行索引
     */
    private int firstRow;

    /**
     * 行索引
     */
    private int lastRow;

    /**
     * 列索引
     */
    private int firstCol;
    /**
     * 列索引
     */
    private int lastCol;
    /**
     * 是否合并
     */
    public boolean isMerged() {
        if (this.getFirstRow() == this.getLastRow() && this.getFirstCol() == this.getLastCol()) {
            return false;
        }
        return true;
    }
}
