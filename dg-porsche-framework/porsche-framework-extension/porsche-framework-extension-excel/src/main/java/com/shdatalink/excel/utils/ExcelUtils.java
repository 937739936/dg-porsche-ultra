package com.shdatalink.excel.utils;

import com.shdatalink.excel.annotation.ExcelProperty;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ExcelUtils {

    /**
     * 导出Excel
     * @param dataList 数据列表
     * @param clazz 实体类类型
     * @return 字节数组（Excel文件内容）
     */
    public static <T> byte[] exportExcel(List<T> dataList, Class<T> clazz) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // 创建工作表
            Sheet sheet = workbook.createSheet(clazz.getSimpleName());
            // 获取并排序字段（按index排序）
            List<Field> fields = getSortedFields(clazz);

            // 创建表头行
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(annotation.name());
            }

            // 填充数据行
            SimpleDateFormat sdf = new SimpleDateFormat();
            for (int rowIdx = 0; rowIdx < dataList.size(); rowIdx++) {
                T data = dataList.get(rowIdx);
                Row dataRow = sheet.createRow(rowIdx + 1); // 从第1行开始（0是表头）

                for (int colIdx = 0; colIdx < fields.size(); colIdx++) {
                    Field field = fields.get(colIdx);
                    field.setAccessible(true);
                    ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
                    Object value = field.get(data);
                    Cell cell = dataRow.createCell(colIdx);

                    // 根据字段类型设置单元格值
                    if (value != null) {
                        if (value instanceof Date) {
                            // 处理日期类型
                            String format = annotation.dateFormat().isEmpty() ? "yyyy-MM-dd" : annotation.dateFormat();
                            sdf.applyPattern(format);
                            cell.setCellValue(sdf.format((Date) value));
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                }
            }

            // 自动调整列宽
            for (int i = 0; i < fields.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Excel导出失败", e);
        }
    }

    /**
     * 导入Excel
     * @param inputStream Excel文件输入流
     * @param clazz 实体类类型
     * @return 解析后的实体列表
     */
    public static <T> List<T> importExcel(InputStream inputStream, Class<T> clazz) {
        List<T> resultList = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            // 获取第一个工作表
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return resultList;
            }

            // 获取并排序字段（按index排序）
            List<Field> fields = getSortedFields(clazz);
            if (fields.isEmpty()) {
                return resultList;
            }

            // 从第1行开始读取数据（0是表头）
            int rowStart = 1;
            int rowEnd = sheet.getLastRowNum();
            SimpleDateFormat sdf = new SimpleDateFormat();

            for (int rowIdx = rowStart; rowIdx <= rowEnd; rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) {
                    continue;
                }

                T entity = clazz.getDeclaredConstructor().newInstance();
                for (int colIdx = 0; colIdx < fields.size(); colIdx++) {
                    Field field = fields.get(colIdx);
                    field.setAccessible(true);
                    ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
                    Cell cell = row.getCell(colIdx);
                    if (cell == null) {
                        continue;
                    }

                    // 根据字段类型设置值
                    String cellValue = getCellValue(cell);
                    if (cellValue == null || cellValue.trim().isEmpty()) {
                        continue;
                    }

                    Class<?> fieldType = field.getType();
                    if (fieldType == String.class) {
                        field.set(entity, cellValue);
                    } else if (fieldType == Integer.class || fieldType == int.class) {
                        field.set(entity, Integer.parseInt(cellValue));
                    } else if (fieldType == Long.class || fieldType == long.class) {
                        field.set(entity, Long.parseLong(cellValue));
                    } else if (fieldType == Date.class) {
                        String format = annotation.dateFormat().isEmpty() ? "yyyy-MM-dd" : annotation.dateFormat();
                        sdf.applyPattern(format);
                        field.set(entity, sdf.parse(cellValue));
                    } else if (fieldType == Double.class || fieldType == double.class) {
                        field.set(entity, Double.parseDouble(cellValue));
                    }
                }
                resultList.add(entity);
            }

            return resultList;
        } catch (ParseException e) {
            throw new RuntimeException("日期格式解析错误", e);
        } catch (Exception e) {
            throw new RuntimeException("Excel导入失败", e);
        }
    }

    /**
     * 获取单元格值（兼容不同类型单元格）
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // 处理数字类型，避免科学计数法
                    return String.valueOf(cell.getNumericCellValue()).replaceAll(".0$", "");
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /**
     * 获取实体类中标记了@ExcelColumn的字段，并按index排序
     */
    private static <T> List<Field> getSortedFields(Class<T> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        List<Field> fieldList = new ArrayList<>();
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(ExcelProperty.class)) {
                fieldList.add(field);
            }
        }
        // 按index排序
        fieldList.sort(Comparator.comparingInt(f -> f.getAnnotation(ExcelProperty.class).index()));
        return fieldList;
    }
}
