package com.shdatalink.excel.model;

import lombok.Data;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.util.List;
import java.util.Map;

/**
 * @author 赵玮
 * @Description 导出文件属性设置
 * @create /2022/3/15
 */
@Data
public class ExcelExportParam {
    /**
     * 文件名称
     * 目前只支持 xlsx  实现别的自己封装补充
     *  例如:重点商品导出
     */
    private String fileName;
    /**
     * 标题
     */
    private String titleName;
    /**
     * 标题行数,默认1
     */
    private int titleRows = 1;
    /**
     * 标题行背景颜色
     */
    private IndexedColors titleForegroundColor ;
    /**
     * 表头背景颜色
     */
    private IndexedColors headForegroundColor ;
    /**
     * 导出
     */
    private Class<?>  sourceClass;
    /**
     * 导出数据
     */
    private List<?> dataList;
    /**
     * 内存数默认1千
     */
    private Integer rowAccessWindowSize=1000;
    /**
     * 每个 sheet的数量 (目前没用，用的话自己封装)
     */
    private Integer sheetSize;

    /**
     * 导出 是否支持换行
     */
    private boolean lineFeed;
    /**
     * 导出 占位符 替换动态表头
     */
    Map<String, Object> placeholderMap;
    /**
     * sheet名字
     */
    private String sheetName;
}
