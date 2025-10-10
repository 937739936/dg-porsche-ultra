package com.shdatalink.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.shdatalink.core.DefaultExcelListener;
import com.shdatalink.core.ExcelListener;
import com.shdatalink.core.ExcelResult;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Excel相关处理
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExcelUtil {

    public static final String SEPARATOR = ",";


    /**
     * 同步导入(适用于小数据量)
     *
     * @param is 输入流
     * @return 转换后集合
     */
    public static <T> List<T> importExcel(InputStream is, Class<T> clazz) {
        return EasyExcel.read(is).head(clazz).autoCloseStream(false).sheet().doReadSync();
    }


    /**
     * 使用校验监听器 异步导入 同步返回
     *
     * @param is         输入流
     * @param clazz      对象类型
     * @param isValidate 是否 Validator 检验 默认为是
     * @return 转换后集合
     */
    public static <T> ExcelResult<T> importExcel(InputStream is, Class<T> clazz, boolean isValidate) {
        DefaultExcelListener<T> listener = new DefaultExcelListener<>(isValidate);
        EasyExcel.read(is, clazz, listener).sheet().doRead();
        return listener.getExcelResult();
    }

    /**
     * 使用自定义监听器 异步导入 自定义返回
     *
     * @param is       输入流
     * @param clazz    对象类型
     * @param listener 自定义监听器
     * @return 转换后集合
     */
    public static <T> ExcelResult<T> importExcel(InputStream is, Class<T> clazz, ExcelListener<T> listener) {
        EasyExcel.read(is, clazz, listener).sheet().doRead();
        return listener.getExcelResult();
    }


    /**
     * 导出excel
     *
     * @param list      导出数据集合
     * @param sheetName 工作表的名称
     * @param clazz     实体类
     */
    public static <T> Response exportExcel(List<T> list, String sheetName, Class<T> clazz) {
        String filename = encodingFilename(sheetName);
        StreamingOutput streamingOutput = outputStream ->
                exportExcel(list, sheetName, clazz, outputStream);

        return Response.ok(streamingOutput)
                .header("Content-Disposition", createContentDisposition(filename))
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8")
                .build();
    }

    /**
     * 导出excel核心方法
     *
     * @param list      导出数据集合
     * @param sheetName 工作表的名称
     * @param clazz     实体类
     * @param os        输出流
     */
    public static <T> void exportExcel(List<T> list, String sheetName, Class<T> clazz, OutputStream os) {
        ExcelWriterSheetBuilder builder = EasyExcel.write(os, clazz)
                .autoCloseStream(false)
                // 自动适配列宽
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet(sheetName);

        builder.doWrite(list);
    }

    /**
     * 编码文件名
     */
    public static String encodingFilename(String filename) {
        return System.currentTimeMillis() + "_" + filename + ".xlsx";
    }

    /**
     * 创建Content-Disposition头信息
     */
    private static String createContentDisposition(String filename) {
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        return "attachment; filename*=UTF-8''" + encoded;
    }

}
