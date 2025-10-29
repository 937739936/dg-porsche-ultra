package com.shdatalink.framework.excel.utils;


import cn.idev.excel.EasyExcel;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.write.builder.ExcelWriterSheetBuilder;
import cn.idev.excel.write.metadata.WriteSheet;
import cn.idev.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.shdatalink.framework.common.utils.file.FileUtil;
import com.shdatalink.framework.excel.core.DefaultExcelListener;
import com.shdatalink.framework.excel.core.ExcelListener;
import com.shdatalink.framework.excel.core.ExcelResult;
import com.shdatalink.framework.excel.model.SheetData;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.io.InputStream;
import java.io.OutputStream;
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

        // 构建并返回响应
        Response.ResponseBuilder responseBuilder = Response.ok(streamingOutput);
        resetResponse(filename, responseBuilder);

        // 构建并返回响应
        return responseBuilder.build();
    }

    /**
     * 导出excel(多sheet)
     */
    public static Response exportExcel(String fileName, List<SheetData> sheetList) {
        if (CollectionUtils.isEmpty(sheetList)) {
            throw new IllegalArgumentException("sheetList is empty");
        }
        // 对文件名进行编码
        String filename = encodingFilename(fileName);

        // 多sheet导出
        StreamingOutput streamingOutput = outputStream -> {
            try (ExcelWriter excelWriter = EasyExcel.write(outputStream).build()) {
                sheetList.forEach(sheetData -> {
                    WriteSheet writeSheet = EasyExcel.writerSheet(sheetData.getSheetNo(), sheetData.getSheetName())
                            .head(sheetData.getClazz())
                            .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                            .build();
                    excelWriter.write(sheetData.getDataList(), writeSheet);
                });
            } catch (Exception e) {
                throw new RuntimeException("Failed to write excel", e);
            }
        };

        // 构建并返回响应
        Response.ResponseBuilder responseBuilder = Response.ok(streamingOutput);
        resetResponse(filename, responseBuilder);
        return responseBuilder.build();
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
        return filename + "_" + System.currentTimeMillis() + ".xlsx";
    }

    /**
     * 重置响应体
     */
    private static void resetResponse(String sheetName, Response.ResponseBuilder responseBuilder) {
        String filename = encodingFilename(sheetName);
        FileUtil.setAttachmentResponseHeader(filename, responseBuilder);
        responseBuilder.header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
    }
}
