package com.shdatalink.excel.utils;

import com.shdatalink.excel.annotation.ExcelProperty;
import jakarta.ws.rs.core.Response;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelUtil {


    /**
     * 导出Excel
     *
     * @param dataList 数据列表
     * @param clazz    数据类型
     * @param <T>      泛型
     * @return Excel文件字节数组
     */
    public static <T> Response exportExcel(List<T> dataList, String sheetName, Class<T> clazz) throws IOException, IllegalAccessException{
        byte[] bytes = exportExcelToByte(dataList, sheetName, clazz);
        String filename = encodingFilename(sheetName);

        return Response.ok(bytes)
                .header("Content-Disposition", createContentDisposition(filename))
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8")
                .build();
    }

    /**
     * 导出Excel
     *
     * @param dataList 数据列表
     * @param clazz    数据类型
     * @param <T>      泛型
     * @return Excel文件字节数组
     */
    public static <T> byte[] exportExcelToByte(List<T> dataList, String sheetName, Class<T> clazz) throws IOException, IllegalAccessException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            // 获取所有带注解的字段并按order排序
            List<Field> fields = getSortedFields(clazz);

            // 创建表头
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);

                Cell cell = headerRow.createCell(i);
                cell.setCellValue(annotation.value());

                // 设置列宽
                if (annotation.width() > 0) {
                    sheet.setColumnWidth(i, annotation.width() * 256);
                }
            }

            // 填充数据
            for (int i = 0; i < dataList.size(); i++) {
                T data = dataList.get(i);
                Row row = sheet.createRow(i + 1);

                for (int j = 0; j < fields.size(); j++) {
                    Field field = fields.get(j);
                    field.setAccessible(true);
                    Object value = field.get(data);

                    Cell cell = row.createCell(j);
                    setCellValue(cell, value, field);
                }
            }

            // 写入字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return outputStream.toByteArray();
        }
    }

    /**
     * 导入Excel
     *
     * @param inputStream Excel文件输入流
     * @param clazz       数据类型
     * @param <T>         泛型
     * @return 数据列表
     */
    public static <T> List<T> importExcel(InputStream inputStream, Class<T> clazz) throws Exception {
        List<T> result = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return result;
            }

            // 获取所有带注解的字段并按order排序
            List<Field> fields = getSortedFields(clazz);
            Map<Integer, Field> columnFieldMap = new HashMap<>();

            // 第一行是表头，用于匹配字段
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return result;
            }

            // 建立列索引到字段的映射
            for (Cell cell : headerRow) {
                String headerName = cell.getStringCellValue().trim();
                int columnIndex = cell.getColumnIndex();

                Optional<Field> fieldOptional = fields.stream()
                        .filter(f -> f.getAnnotation(ExcelProperty.class).value().equals(headerName))
                        .findFirst();

                fieldOptional.ifPresent(field -> columnFieldMap.put(columnIndex, field));
            }

            // 读取数据行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                T instance = clazz.getDeclaredConstructor().newInstance();

                for (Map.Entry<Integer, Field> entry : columnFieldMap.entrySet()) {
                    int columnIndex = entry.getKey();
                    Field field = entry.getValue();
                    Cell cell = row.getCell(columnIndex);

                    if (cell != null) {
                        Object value = getCellValue(cell, field);
                        field.setAccessible(true);
                        field.set(instance, value);
                    }
                }

                result.add(instance);
            }
        }

        return result;
    }

    /**
     * 获取排序后的字段列表
     */
    private static <T> List<Field> getSortedFields(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ExcelProperty.class))
                .sorted(Comparator.comparingInt(f -> f.getAnnotation(ExcelProperty.class).order()))
                .collect(Collectors.toList());
    }

    /**
     * 设置单元格值
     */
    private static void setCellValue(Cell cell, Object value, Field field) {
        ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);

        if (value == null) {
            cell.setCellValue("");
            return;
        }

        if (value instanceof Date) {
            CellStyle cellStyle = cell.getSheet().getWorkbook().createCellStyle();
            CreationHelper createHelper = cell.getSheet().getWorkbook().getCreationHelper();
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(annotation.dateFormat()));
            cell.setCellStyle(cellStyle);
            cell.setCellValue((Date) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * 获取单元格值
     */
    private static Object getCellValue(Cell cell, Field field) {
        ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
        Class<?> fieldType = field.getType();

        try {
            switch (cell.getCellType()) {
                case STRING:
                    String stringValue = cell.getStringCellValue().trim();
                    if (stringValue.isEmpty()) {
                        return null;
                    }

                    if (fieldType == String.class) {
                        return stringValue;
                    } else if (fieldType == Integer.class || fieldType == int.class) {
                        return Integer.parseInt(stringValue);
                    } else if (fieldType == Long.class || fieldType == long.class) {
                        return Long.parseLong(stringValue);
                    } else if (fieldType == Double.class || fieldType == double.class) {
                        return Double.parseDouble(stringValue);
                    } else if (fieldType == Boolean.class || fieldType == boolean.class) {
                        return Boolean.parseBoolean(stringValue);
                    } else if (fieldType == Date.class) {
                        SimpleDateFormat sdf = new SimpleDateFormat(annotation.dateFormat());
                        return sdf.parse(stringValue);
                    }
                    break;

                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        if (fieldType == Date.class) {
                            return cell.getDateCellValue();
                        } else if (fieldType == String.class) {
                            SimpleDateFormat sdf = new SimpleDateFormat(annotation.dateFormat());
                            return sdf.format(cell.getDateCellValue());
                        }
                    } else {
                        double numericValue = cell.getNumericCellValue();
                        if (fieldType == Integer.class || fieldType == int.class) {
                            return (int) numericValue;
                        } else if (fieldType == Long.class || fieldType == long.class) {
                            return (long) numericValue;
                        } else if (fieldType == Double.class || fieldType == double.class) {
                            return numericValue;
                        } else if (fieldType == String.class) {
                            return String.valueOf((long) numericValue);
                        }
                    }
                    break;

                case BOOLEAN:
                    boolean booleanValue = cell.getBooleanCellValue();
                    if (fieldType == Boolean.class || fieldType == boolean.class) {
                        return booleanValue;
                    } else if (fieldType == String.class) {
                        return String.valueOf(booleanValue);
                    }
                    break;

                case BLANK:
                    return null;

                default:
                    return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse cell value for field: " + field.getName(), e);
        }

        throw new IllegalArgumentException("Unsupported field type: " + fieldType.getName());
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
