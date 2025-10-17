package com.shdatalink.excel.utils;


import com.shdatalink.excel.annotation.ExcelProperty;
import com.shdatalink.excel.model.ExcelExportParam;
import com.shdatalink.excel.model.ExcelGroupVo;
import com.shdatalink.excel.model.ExcelImportVo;
import com.shdatalink.json.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author 赵玮
 * @Description 封装poi导入导出(由于项目使用poi只能自己封装一套导入导出)
 * 1 不加样式复杂功能 poi版本的不同样式代码是不兼容,样式耗内存
 * 2 本工具类基本满足了日常需求，需要改动源码，尽量阅读一下，导入和导出有一些关联(保守情况 复制一份自己改)
 * @create /2022/3/15
 */
@Slf4j
public class ExcelUtil {

    private static final String SPILT_START_END = "_";
    private static final String SPILT_MAX = ",";
    private static final String SPILT_MARK = "~";

    private static final String SUFFIX_XLSX = ".xlsx";
    private static final String SUFFIX_XLS = ".xls";

    //每个sheet最大一百万
    private static final int DATA_SIZE = 1000000;
    //导出excel时内存数
    private static final int rowAccessWindowSize = 1000;
    private static final int BYTE_SIZE = 1024;

    private static final Pattern pattern = Pattern.compile("\\{(.*?)\\}");
    private static Matcher matcher;

    /**
     * 导入文件 csv  xlsx  xls
     * 导入10万 数据量大尽量用sax
     *
     * @param in
     * @return
     */
    public static List readExcel(InputStream in, ExcelImportVo excelImportVo) throws Exception {
        //1 校验流
        //  String suffix = choiceFile(in);
        List result = new ArrayList<>();
        if (in == null) {
            log.error("class ExcelUtil  InputStream  error");
            return result;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[BYTE_SIZE];
        int len;
        while ((len = in.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        InputStream userIs = new ByteArrayInputStream(baos.toByteArray());
        Workbook book = WorkbookFactory.create(userIs);
   /*     Iterator<Sheet> sheetIterator = book.sheetIterator();
        while (sheetIterator.hasNext()) {
            Sheet next =sheetIterator.next();
            excelDataAdd(result, next, excelImportVo);
        }*/
        int numberOfSheets = book.getNumberOfSheets();
        boolean firstTitle = false;
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = book.getSheetAt(i);
            excelDataAdd(result, sheet, excelImportVo, i);
            firstTitle = true;
        }
       /*   boolean isXSSFWorkbook = !(book instanceof HSSFWorkbook);
          for (int i = excelImportVo.getStartSheetIndex(); i < excelImportVo.getStartSheetIndex()+ excelImportVo.getSheetNum(); i++) {
              if (isXSSFWorkbook) {
                  pictures = PoiPublicUtil.getSheetPictrues07((XSSFSheet) book.getSheetAt(i),(XSSFWorkbook) book);

              } else {
                  pictures = PoiPublicUtil.getSheetPictrues03((HSSFSheet) book.getSheetAt(i),(HSSFWorkbook) book);
                  excelDataAdd(result,(HSSFSheet) book.getSheetAt(i), excelImportVo);
              }
              excelDataAdd(result,book.getSheetAt(i),excelImportVo);
          }*/
        return result;
    }


    /**
     * 校验流
     *
     * @param in
     * @return
     */
    private String choiceFile(InputStream in) {
        try {
            if (!in.markSupported()) {
                return null;
            }
            FileMagic fileMagic = FileMagic.valueOf(in);
            if (FileMagic.OLE2.equals(fileMagic)) {
                return SUFFIX_XLS;
            }
            if (FileMagic.OOXML.equals(fileMagic)) {
                return SUFFIX_XLSX;
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 导出excel(千万级数据导出,不会内存溢出)
     * 1 谨记不加任何样式(低版本和高版本 样式代码不兼容)
     * 2 目前实现，数据脱敏,数据库枚举转换,日期格式化, 字段动态展示及排序, 其他功能需要自己实现
     * 3 目前导出仅支持 .xlsx 其他功能自己实现
     *
     * @param response
     * @param excelExportParam
     * @throws IOException
     */
    public static void exportExcel(HttpServletResponse response, ExcelExportParam excelExportParam) throws Exception {

        String name = excelExportParam.getFileName();
        int titleRows = excelExportParam.getTitleRows();
        if (titleRows < 0) {
            throw new Exception("标题行数不能小于0");
        }
        //设置响应格式名称等
        responseContentType(response, name);
        Workbook work = buildExcel(excelExportParam);
        //响应流返回
        responseWrite(response, work);
    }

    /**
     * 导出excel实现一个Sheet一个模版数据
     *
     * @param response
     * @param excelExportParamList
     * @throws IOException
     */
    public static void exportExcelMoreSheet(HttpServletResponse response, List<ExcelExportParam> excelExportParamList) throws Exception {
        String name = excelExportParamList.get(0).getFileName();
        for (ExcelExportParam excelExportParam : excelExportParamList) {
            int titleRows = excelExportParam.getTitleRows();
            if (titleRows < 0) {
                throw new Exception("标题行数不能小于0");
            }
        }
        //设置响应格式名称等
        responseContentType(response, name);
        Workbook work = buildExcelMoreSheet(excelExportParamList);
        //响应流返回
        responseWrite(response, work);

    }


    /**
     * 报表下载
     *
     * @param excelExportVo Excel对象
     */
/*    private void exportExcel2(HttpServletResponse response, ExcelExportVo excelExportVo) throws IOException {
        //查询的数据
        List<?> dataList = excelExportVo.getDataList();
        Class<?> sourceClass = excelExportVo.getSourceClass();
        String fileName = excelExportVo.getFileName();
        //内存中1000条 超过就进入临时文件
        List<Field> column = getColumn(sourceClass);
        Workbook work = buildExcel(dataList, column);
        String filename = URLEncoder.encode(fileName, "utf-8");
        //浏览器下载
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);
        response.flushBuffer();
        ServletOutputStream outputStream = response.getOutputStream();
        work.write(outputStream);

    }*/

    /**
     * 报表导入  空行注意，校验字段是否必填  ，字段的不同类型 , 导入文件格式两种，
     *
     * @param result Excel对象
     */
    private static void excelDataAdd(List<Object> result, Sheet sheet, ExcelImportVo excelImportVo, int sheetIndex) throws Exception {
        if (sheet == null) {
            log.info("ExcelUtil importExcel Sheet is null");
            return;
        }
        Class<?> pojoClass = excelImportVo.getPojoClass();
        int titleRows = excelImportVo.getTitleRows();
        Iterator<Row> rows = sheet.rowIterator();
        //索引最大数
        int lastRowNum = sheet.getLastRowNum();
        //行头数
        int headRows = 1;
        if (mergedGroupName(getColumn(pojoClass))) {
            //有组名
            headRows = 2;
        }
        Map<Integer, Field> titleFileMap = null;
        Map<Integer, String> headMap = new LinkedHashMap<Integer, String>();
        for (int i = titleRows; i < titleRows + headRows; i++) {
            Row row = sheet.getRow(i);
            //导入excel的列名及顺序
            getHeadMap(row, excelImportVo, headMap);
        }
        //获取属性值映射赋值
        titleFileMap = getTitleFileMap(headMap, excelImportVo);
        // 循环行Row
        Object object = null;
        for (int i = titleRows + headRows; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            //创建对象
            object = createObject(pojoClass);
            //按照excel列排序
            Set<Integer> keys = titleFileMap.keySet();
            for (Integer cn : keys) {
                Cell cell = row.getCell(cn);
                Field field = titleFileMap.get(cn);
                /**
                 * 反射给属性赋值
                 */
                saveFieldValue(field, cell, object);
            }
            result.add(object);
        }
    }

    /**
     * 获取报表注解并排序
     *
     * @param cls 模板类
     * @param <T>
     * @return
     */
    private static <T> List<Field> getColumn(Class<T> cls) {
        Field[] fields = cls.getDeclaredFields();
        return Arrays.stream(fields).filter(field -> null != field.getAnnotation(ExcelProperty.class))
                .sorted(Comparator.comparing(field -> {
                    int col = 0;
                    ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
                    if (annotation != null) {
                        col = annotation.orderNum();
                    }
                    return col;
                })).collect(Collectors.toList());
    }

    /**
     * 获取name集合
     *
     * @param params
     * @return
     */
    private static <T> List<String> getNameList(ExcelImportVo params) {
        Class<?> cls = params.getPojoClass();
        Field[] fields = cls.getDeclaredFields();
        Map<String, Object> placeholderMap = params.getPlaceholderMap();
        return Arrays.stream(fields).filter(field -> null != field.getAnnotation(ExcelProperty.class))
                .map(a -> replaceWithMap(a.getAnnotation(ExcelProperty.class).name().replace(" ", ""), placeholderMap)).collect(Collectors.toList());
    }

    /**
     * @param excelExportParam
     */
    private static Workbook buildExcel(ExcelExportParam excelExportParam) {
        //查询的数据
        List<?> dataList = excelExportParam.getDataList();
        Class<?> sourceClass = excelExportParam.getSourceClass();
        //内存中1000条 超过就进入临时文件
        List<Field> fieldList = getColumn(sourceClass);
        //创建Excel工作簿对象
        Workbook workbook = new SXSSFWorkbook(rowAccessWindowSize);
        //空数据处理
        if (CollectionUtils.isEmpty(dataList)) {
            Sheet sheet = buildHeader(fieldList, workbook, "Sheet1", excelExportParam);
            //冻结关闭 要不合并单元格时有横线
            //  workbook.getSheet("Sheet1").createFreezePane(0, 1, 0, 1);
            return workbook;
        }


        List<? extends List<?>> partition = ListUtils.partition(dataList, DATA_SIZE);
        AtomicInteger sheetSize = new AtomicInteger(1);
        for (List<?> groupList : partition) {
            int dataIndex = 1;
            Sheet sheet = buildHeader(fieldList, workbook, getSheetName(excelExportParam.getSheetName(), sheetSize), excelExportParam);
            if (mergedTitle(excelExportParam)) {
                dataIndex = dataIndex + excelExportParam.getTitleRows();
            }
            if (mergedGroupName(fieldList)) {
                dataIndex = dataIndex + 1;
            }
            AtomicInteger ai = new AtomicInteger(dataIndex);
            //列索引
            AtomicInteger excelColumnIndex = new AtomicInteger();
            Workbook finalWorkbook = workbook;
            groupList.forEach(t -> {
                Row row1 = sheet.createRow(ai.getAndIncrement());
                AtomicInteger aj = new AtomicInteger();
                fieldList.forEach(field -> {
                    field.setAccessible(true);
                    Object value = "";
                    ExcelProperty annotation = null;
                    try {
                        value = field.get(t);
                        if (value == null) {
                            aj.getAndIncrement();
                            return;
                        }
                        annotation = field.getAnnotation(ExcelProperty.class);
                        String format = annotation.exportFormat();
                        if (StringUtils.isNotEmpty(format)) {
                            value = dateFormatValue(value, format);
                        }
                        String[] exportReplace = annotation.exportReplace();
                        if (exportReplace != null && exportReplace.length > 0) {
                            value = replaceValue(exportReplace, value.toString());
                        }
                        String suffix = annotation.exportSuffix();
                        if (StringUtils.isNotEmpty(suffix)) {
                            value = value + suffix;
                        }
                        String rule = annotation.desensitizationRule();
                        if (StringUtils.isNotEmpty(rule)) {
                            value = desensitization(rule, value);
                        }

                    } catch (IllegalAccessException e) {
                        e.getMessage();
                    }
                    Cell cell = row1.createCell(aj.getAndIncrement());
                    //样式处理
                    CellStyle cellStyle = createCellStyle(sheet, finalWorkbook, annotation, excelExportParam, excelColumnIndex);
                    if (null != cellStyle) {
                        cell.setCellStyle(cellStyle);
                    }
                    cell.setCellValue(value.toString());

                    excelColumnIndex.getAndIncrement();
                });
            });
            // workbook.getSheet("Sheet" + andIncrement).createFreezePane(0, 1, 0, 1);
        }
        return workbook;

    }

    /**
     * 构建
     *
     * @param excelExportParamList
     * @return
     */
    private static Workbook buildExcelMoreSheet(List<ExcelExportParam> excelExportParamList) {
        //创建Excel工作簿对象
        Workbook workbook = new SXSSFWorkbook(rowAccessWindowSize);
        AtomicInteger sheetNum = new AtomicInteger(1);
        for (ExcelExportParam excelExportParam : excelExportParamList) {
            //查询的数据
            List<?> dataList = excelExportParam.getDataList();
            Class<?> sourceClass = excelExportParam.getSourceClass();
            //内存中1000条 超过就进入临时文件
            List<Field> fieldList = getColumn(sourceClass);
            Sheet sheet =
                    buildHeaderMoreSheet(fieldList, workbook, getSheetName(excelExportParam.getSheetName(), sheetNum),
                            excelExportParam);
            //空数据处理
            if (CollectionUtils.isEmpty(dataList)) {
                continue;
            }

            int dataIndex = 1;

            if (mergedTitle(excelExportParam)) {
                dataIndex = dataIndex + excelExportParam.getTitleRows();
            }
            if (mergedGroupName(fieldList)) {
                dataIndex = dataIndex + 1;
            }
            AtomicInteger ai = new AtomicInteger(dataIndex);
            //列索引
            AtomicInteger excelColumnIndex = new AtomicInteger();
            Workbook finalWorkbook = workbook;
            for (Object t : dataList) {
                Row row1 = sheet.createRow(ai.getAndIncrement());
                AtomicInteger aj = new AtomicInteger();
                fieldList.forEach(field -> {
                    field.setAccessible(true);
                    Object value = null;
                    ExcelProperty annotation = null;
                    try {
                        value = field.get(t);
                        if (value == null) {
                            aj.getAndIncrement();
                            return;
                        }
                        annotation = field.getAnnotation(ExcelProperty.class);
                        String format = annotation.exportFormat();
                        if (StringUtils.isNotEmpty(format)) {
                            value = dateFormatValue(value, format);
                        }
                        String[] exportReplace = annotation.exportReplace();
                        if (exportReplace != null && exportReplace.length > 0) {
                            value = replaceValue(exportReplace, value.toString());
                        }
                        String suffix = annotation.exportSuffix();
                        if (StringUtils.isNotEmpty(suffix)) {
                            value = value + suffix;
                        }
                        String rule = annotation.desensitizationRule();
                        if (StringUtils.isNotEmpty(rule)) {
                            value = desensitization(rule, value);
                        }

                    } catch (IllegalAccessException e) {
                        e.getMessage();
                    }
                    Cell cell = row1.createCell(aj.getAndIncrement());
                    //样式处理
                    CellStyle cellStyle =
                            createCellStyle(sheet, finalWorkbook, annotation, excelExportParam, excelColumnIndex);
                    if (null != cellStyle) {
                        cell.setCellStyle(cellStyle);
                    }
                    cell.setCellValue(value.toString());
                    excelColumnIndex.getAndIncrement();
                });
            }
        }

        return workbook;
    }

    /**
     * 获取sheet名字
     *
     * @param sheetName
     * @param sheetNum
     * @return
     */
    private static String getSheetName(String sheetName, AtomicInteger sheetNum) {
        if (StringUtils.isEmpty(sheetName)) {
            return "Sheet" + sheetNum.getAndIncrement();
        }
        return sheetName;
    }

    /**
     * 样式的构建
     *
     * @param finalWorkbook
     * @param annotation
     * @param excelExportParam
     * @return
     */
    private static CellStyle createCellStyle(Sheet sheet, Workbook finalWorkbook, ExcelProperty annotation, ExcelExportParam excelExportParam, AtomicInteger excelColumnIndex) {
        CellStyle cellStyle = null;
        if (excelExportParam.isLineFeed()) {
            if (null == cellStyle) {
                cellStyle = finalWorkbook.createCellStyle();
            }
            // 自动换行
            cellStyle.setWrapText(true);
        }
        //宽
        if (annotation.width() >= 1 && annotation.width() <= 255) {
            if (null == cellStyle) {
                cellStyle = finalWorkbook.createCellStyle();
            }

            sheet.setColumnWidth(excelColumnIndex.get(), 256 * annotation.width());
        }


        return cellStyle;
    }

    /**
     * 创建行的头
     *
     * @param fieldList
     * @param workbook
     * @return
     */
    private static Sheet buildHeader(List<Field> fieldList, Workbook workbook, String sheetName,
                                     ExcelExportParam excelExportParam) {

        int titleRows = excelExportParam.getTitleRows();
        Sheet sheet = workbook.createSheet(sheetName);
        int indexHead = 0;
        //合并标题单元格
        if (mergedTitle(excelExportParam)) {
            indexHead = indexHead + titleRows;
            mergedRegionUnsafe(workbook, sheet, excelExportParam, fieldList.size() - 1);
        }

        //有分组的时候
        if (mergedGroupName(fieldList)) {
            buildHeaderWithGroupName(workbook, sheet, excelExportParam, fieldList, indexHead);
        } else {
            //无分组时候
            // 创建Excel工作表的行
            Row row = sheet.createRow(indexHead);
            AtomicInteger aj = new AtomicInteger();
            AtomicInteger aj2 = new AtomicInteger();
            //创建公共样式
            CellStyle cellStyle = createPublicCellStyle(workbook);
            //添加背景色
            createForegroundColor(cellStyle, excelExportParam.getHeadForegroundColor());
            for (Field field : fieldList) {
                ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
                aj2.getAndIncrement();
                Cell cell = row.createCell(aj.getAndIncrement());
                cell.setCellValue(getAfterReplacementName(annotation, excelExportParam));
                cell.setCellStyle(cellStyle);
            }
        }
        return sheet;
    }

    /**
     * 背景色添加
     *
     * @param cellStyle
     * @param headForegroundColor
     */
    private static void createForegroundColor(CellStyle cellStyle, IndexedColors headForegroundColor) {
        if (Objects.nonNull(cellStyle) && Objects.nonNull(headForegroundColor)) {
            // 设置背景色
            cellStyle.setFillForegroundColor(headForegroundColor.getIndex());
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
    }

    /**
     * 公共的样式
     *
     * @param workbook
     * @return
     */
    private static CellStyle createPublicCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER); // 居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直居中
        cellStyle.setWrapText(true);// 自动换行
        return cellStyle;
    }


    /**
     * 创建行的头
     *
     * @param fieldList
     * @param workbook
     * @return
     */
    private static Sheet buildHeaderMoreSheet(List<Field> fieldList, Workbook workbook, String sheetName,
                                              ExcelExportParam excelExportParam) {

        int titleRows = excelExportParam.getTitleRows();
        Sheet sheet = workbook.createSheet(sheetName);
        int indexHead = 0;
        //合并标题单元格
        if (mergedTitle(excelExportParam)) {
            indexHead = indexHead + titleRows;
            mergedRegionUnsafe(workbook, sheet, excelExportParam, fieldList.size() - 1);
        }

        //有分组的时候
        if (mergedGroupName(fieldList)) {
            buildHeaderWithGroupName(workbook, sheet, excelExportParam, fieldList, indexHead);
        } else {//无分组时候
            // 创建Excel工作表的行
            Row row = sheet.createRow(indexHead);
            AtomicInteger aj = new AtomicInteger();
            AtomicInteger aj2 = new AtomicInteger();
            for (Field field : fieldList) {
                ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
                aj2.getAndIncrement();
                Cell cell = row.createCell(aj.getAndIncrement());
                cell.setCellValue(getAfterReplacementName(annotation, excelExportParam));
            }
        }
        return sheet;
    }

    /**
     * 构建带分组的数据
     *
     * @param workbook
     * @param sheet
     * @param excelExportParam
     * @param fieldList
     * @param indexHead
     */
    private static void buildHeaderWithGroupName(Workbook workbook, Sheet sheet, ExcelExportParam excelExportParam, List<Field> fieldList, int indexHead) {
        //合并表头索引
        HashMap<String, ExcelGroupVo> nameMergedHashMap = mergedGroupNameIndexMap(fieldList, indexHead);

        // 创建Excel工作表的行
        Row row = sheet.createRow(indexHead);
        Row row2 = sheet.createRow(indexHead + 1);
        AtomicInteger aj = new AtomicInteger();
        AtomicInteger aj2 = new AtomicInteger();
        for (Field field : fieldList) {
            //创建公共样式
            CellStyle cellStyle = createPublicCellStyle(workbook);
            //添加背景色
            createForegroundColor(cellStyle, excelExportParam.getHeadForegroundColor());
            ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
            String name = annotation.name();

            if (StringUtils.isNotEmpty(annotation.groupName())) {
                Cell cell = row.createCell(aj.getAndIncrement());
                cell.setCellValue(getAfterReplacementGroupName(annotation, excelExportParam));
                if (nameMergedHashMap.containsKey(getMergedGroupNameIndexKey(field))) {
                    ExcelGroupVo excelGroupVo = nameMergedHashMap.get(getMergedGroupNameIndexKey(field));
                    if (excelGroupVo.isMerged()) {
                        sheet.addMergedRegionUnsafe(new CellRangeAddress(excelGroupVo.getFirstRow(), excelGroupVo.getLastRow(), excelGroupVo.getFirstCol(), excelGroupVo.getLastCol()));
                    }
                    cell.setCellStyle(cellStyle);
                }
                Cell cell2 = row2.createCell(aj2.getAndIncrement());
                cell2.setCellValue(getAfterReplacementName(annotation, excelExportParam));
                cell2.setCellStyle(cellStyle);
            } else {
                aj2.getAndIncrement();
                Cell cell = row.createCell(aj.getAndIncrement());
                cell.setCellValue(getAfterReplacementName(annotation, excelExportParam));
                cell.setCellStyle(cellStyle);
                ExcelGroupVo excelGroupVo = nameMergedHashMap.get(getMergedGroupNameIndexKey(field));
                sheet.addMergedRegionUnsafe(new CellRangeAddress(excelGroupVo.getFirstRow(), excelGroupVo.getLastRow(), excelGroupVo.getFirstCol(), excelGroupVo.getLastCol()));
            }
        }

    }

    /**
     * 替换表头占位符
     *
     * @param annotation
     * @param excelExportParam
     * @return
     */
    private static String getAfterReplacementName(ExcelProperty annotation, ExcelExportParam excelExportParam) {
        Map<String, Object> placeholderMap = excelExportParam.getPlaceholderMap();
        if (null != placeholderMap) {
            return replaceWithMap(annotation.name().replace(" ", ""), placeholderMap);
        }
        return annotation.name().replace(" ", "");
    }

    /**
     * 替换组名称占位符
     *
     * @param annotation
     * @param excelExportParam
     * @return
     */
    private static String getAfterReplacementGroupName(ExcelProperty annotation, ExcelExportParam excelExportParam) {
        Map<String, Object> placeholderMap = excelExportParam.getPlaceholderMap();
        if (null != placeholderMap) {
            return replaceWithMap(annotation.groupName().replace(" ", ""), placeholderMap);
        }
        return annotation.groupName().replace(" ", "");
    }

    /**
     * 获取合并表头最后一列索引
     *
     * @param fieldList
     * @return
     */
    private static int mergedGroupNameIndexLastCol(List<Field> fieldList, String groupName) {
        int a = -1;
        for (Field field : fieldList) {
            ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
            if (StringUtils.isNotEmpty(annotation.groupName()) && annotation.groupName().equals(groupName)) {
                a++;
            }
        }
        return a;
    }

    /**
     * 获取合并表头索引
     *
     * @param fieldList
     * @return
     */
    private static HashMap<String, ExcelGroupVo> mergedGroupNameIndexMap(List<Field> fieldList, int indexHead) {
        HashMap<String, ExcelGroupVo> hashMap = new LinkedHashMap<>();
        List<String> groupNameList = new ArrayList<>();
        int indexCol = 0;
        for (Field field : fieldList) {
            ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
            String groupName = annotation.groupName();
            String name = annotation.name();
            ExcelGroupVo excelGroupVo = new ExcelGroupVo();
            excelGroupVo.setFirstRow(indexHead);
            excelGroupVo.setName(name);
            if (StringUtils.isNotEmpty(groupName)) {
                if (!groupNameList.contains(groupName)) {
                    excelGroupVo.setLastRow(indexHead);
                    excelGroupVo.setFirstCol(indexCol);
                    //最新索引
                    indexCol = indexCol + mergedGroupNameIndexLastCol(fieldList, groupName);
                    excelGroupVo.setLastCol(indexCol);
                    groupNameList.add(groupName);
                } else {
                    groupNameList.add(groupName);
                    continue;
                }
            } else {
                excelGroupVo.setLastRow(indexHead + 1);
                excelGroupVo.setFirstCol(indexCol);
                excelGroupVo.setLastCol(indexCol);
            }
            indexCol++;
            hashMap.put(getMergedGroupNameIndexKey(field), excelGroupVo);
        }
        return hashMap;
    }

    private static String getMergedGroupNameIndexKey(Field field) {
        ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
        String name = annotation.name();
        return name + "_" + field.getName();
    }

    /**
     * 是否有合并主题
     *
     * @param excelExportParam
     * @return
     */
    private static Boolean mergedTitle(ExcelExportParam excelExportParam) {
        String titleName = excelExportParam.getTitleName();
        int titleRows = excelExportParam.getTitleRows();
        return titleRows != 0 && StringUtils.isNotEmpty(titleName);
    }

    /**
     * 是否有组名
     *
     * @param fieldList
     * @return
     */
    private static Boolean mergedGroupName(List<Field> fieldList) {
        for (Field field : fieldList) {
            ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
            if (StringUtils.isNotEmpty(annotation.groupName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 合并单元格
     *
     * @param workbook
     * @param sheet
     * @param excelExportParam
     * @param lastCol          列索引
     */
    private static void mergedRegionUnsafe(Workbook workbook, Sheet sheet, ExcelExportParam excelExportParam,
                                           int lastCol) {
        // 标题
        String title = excelExportParam.getTitleName();
        // 标题占的行数
        int titleRows = excelExportParam.getTitleRows();
        //创建公共样式
        CellStyle cellStyle = createPublicCellStyle(workbook);
        //添加背景色
        createForegroundColor(cellStyle, excelExportParam.getTitleForegroundColor());
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(cellStyle);
        sheet.addMergedRegionUnsafe(new CellRangeAddress(0, titleRows - 1, 0, lastCol));
    }

    /**
     * 设置字体颜色-根据value
     *
     * @param workbook
     */
    private static void setColor(Workbook workbook, CellStyle cellStyle) {
        // 创建字体对象
        Font rowFont = workbook.createFont();
        // 将字体应用到样式中
        cellStyle.setFont(rowFont);

    }

    /**
     * @param rule 规则
     * @param data 数据
     *             数据脱敏规则
     *             规则1: 采用保留头和尾的方式,中间数据加星号
     *             如: 身份证  6_4 则保留 370101********1234
     *             手机号   3_4 则保留 131****1234
     *             规则2: 采用确定隐藏字段的进行隐藏,优先保留头
     *             如: 姓名   1,3 表示最大隐藏3位,最小一位
     *             李 -->  *
     *             李三 --> 李*
     *             张全蛋  --> 张*蛋
     *             李张全蛋 --> 李**蛋
     *             尼古拉斯.李张全蛋 -> 尼古拉***张全蛋
     *             规则3: 特殊符号后保留
     *             如: 邮箱    1~@ 表示只保留第一位和@之后的字段
     *             afterturn@wupaas.com -> a********@wupaas.com
     */
    private static String desensitization(String rule, Object data) {
        String value = data.toString();
        if (rule.contains(SPILT_START_END)) {
            String[] arr = rule.split(SPILT_START_END);
            return subStartEndString(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), value);

        }
        if (rule.contains(SPILT_MAX)) {
            String[] arr = rule.split(SPILT_MAX);
            return subMaxString(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), value);

        }
        if (rule.contains(SPILT_MARK)) {
            String[] arr = rule.split(SPILT_MARK);
            return markSpilt(Integer.parseInt(arr[0]), arr[1], value);

        }
        return value;
    }

    /**
     * 收尾截取数据
     *
     * @param start
     * @param end
     * @param value
     * @return
     */
    private static String subStartEndString(int start, int end, String value) {
        if (value == null) {
            return null;
        }
        if (value.length() <= start + end) {
            return value;
        }
        return StringUtils.left(value, start).concat(StringUtils.leftPad(StringUtils.right(value, end), StringUtils.length(value) - start, "*"));
    }

    /**
     * 部分数据截取，优先对称截取
     *
     * @param start
     * @param end
     * @param value
     * @return
     */
    private static String subMaxString(int start, int end, String value) {
        if (value == null) {
            return null;
        }
        if (start > end) {
            throw new IllegalArgumentException("start must less end");
        }
        int len = value.length();
        if (len <= start) {
            return StringUtils.leftPad("", len, "*");
        } else if (len > start && len <= end) {
            if (len == 1) {
                return value;
            }
            if (len == 2) {
                return StringUtils.left(value, 1).concat("*");
            }
            return StringUtils.left(value, 1)
                    .concat(StringUtils.leftPad(StringUtils.right(value, 1), StringUtils.length(value) - 1, "*"));
        } else {
            start = (int) Math.ceil((len - end + 0.0D) / 2);
            end = len - start - end;
            end = end == 0 ? 1 : end;
            return StringUtils.left(value, start)
                    .concat(StringUtils.leftPad(StringUtils.right(value, end), len - start, "*"));
        }
    }

    /**
     * 特定字符分隔，添加星号
     *
     * @param start
     * @param mark
     * @param value
     * @return
     */
    private static String markSpilt(int start, String mark, String value) {
        if (value == null) {
            return null;
        }
        int end = value.lastIndexOf(mark);
        if (end <= start) {
            return value;
        }
        return StringUtils.left(value, start)
                .concat(StringUtils.leftPad(StringUtils.right(value, value.length() - end), value.length() - start, "*"));
    }

/*    private Object numFormatValue(Object value, String format) {
        if (value == null) {
            return null;
        }
        if (!NumberUtils.isNumber(value.toString())) {
            log.error("ExcelUtil data want num format ,but is not num, value is:" + value);
            return null;
        }
        Double        d  = Double.parseDouble(value.toString());
        DecimalFormat df = new DecimalFormat(format);
        return df.format(d);
    }*/

    private static Object dateFormatValue(Object value, String formatStr) {
        Date temp = null;
      /*  if (value instanceof String && StringUtils.isNoneEmpty(value.toString())) {
            SimpleDateFormat format = new SimpleDateFormat(entity.getDatabaseFormat());
            temp = format.parse(value.toString());
        } else*/
        if (value instanceof Date) {
            temp = (Date) value;
        } else if (value instanceof Instant) {
            Instant instant = (Instant) value;
            temp = Date.from(instant);
        } else if (value instanceof LocalDate) {
            LocalDate localDate = (LocalDate) value;
            temp = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } else if (value instanceof LocalDateTime) {
            LocalDateTime localDateTime = (LocalDateTime) value;
            temp = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        } else if (value instanceof ZonedDateTime) {
            ZonedDateTime zonedDateTime = (ZonedDateTime) value;
            temp = Date.from(zonedDateTime.toInstant());
        } else if (value instanceof OffsetDateTime) {
            OffsetDateTime offsetDateTime = (OffsetDateTime) value;
            temp = Date.from(offsetDateTime.toInstant());
        } else if (value instanceof java.sql.Date) {
            temp = new Date(((java.sql.Date) value).getTime());
        } else if (value instanceof java.sql.Time) {
            temp = new Date(((java.sql.Time) value).getTime());
        } else if (value instanceof java.sql.Timestamp) {
            temp = new Date(((java.sql.Timestamp) value).getTime());
        }
        if (temp != null) {
            SimpleDateFormat format = new SimpleDateFormat(formatStr);
       /*     if (StringUtils.isNotEmpty(entity.getTimezone())) {
                format.setTimeZone(TimeZone.getTimeZone(entity.getTimezone()));
            }*/
            value = format.format(temp);
        }
        return value;
    }

    private static Object replaceValue(String[] replace, String value) {
        String[] temp;
        for (String str : replace) {
            temp = str.split("_");
            if (value.equals(temp[1])) {
                value = temp[0];
                break;
            }
        }
        return value;
    }

    /**
     * 导入获取表格字段列名对应信息
     */
    private static Map<Integer, String> getHeadMap(Row row, ExcelImportVo params, Map<Integer, String> headMap) throws Exception {
        List<String> nameList = getNameList(params);
        // 获取表头
        Iterator<Cell> cellTitle = row.cellIterator();
        while (cellTitle.hasNext()) {
            Cell cell = cellTitle.next();
            String value = getKeyValue(cell);
            int i = cell.getColumnIndex();
            if (StringUtils.isNotEmpty(value) && nameList.contains(value)) {
                // 自己验证一下
                if (headMap.containsKey(i)) {
                    throw new Exception("不支持重名导入");
                }
                headMap.put(i, value);
            }
        }
        return headMap;
    }

    /**
     * 获取key的值,针对不同类型获取不同的值
     *
     * @author JueYue 2013-11-21
     */
    private static String getKeyValue(Cell cell) {
        Object obj = getCellValue(cell);
        return obj == null ? null : obj.toString().trim();
    }

    /**
     * 获取单元格的值
     *
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == CellType.FORMULA) {
            return cell.getCellFormula();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        } else {
            cell.setCellType(CellType.STRING);
            return cell.getStringCellValue();
        }

    }

    /**
     * 反射构造对象
     *
     * @param clazz
     * @return
     */
    private static Object createObject(Class<?> clazz) {
        Object obj = null;
        try {
            obj = clazz.newInstance();
         /*   Field[] fields = getClassFields(clazz);
            for (Field field : fields) {
                if (field.getAnnotation(MyExcel.class) == null&&field.getAnnotation(MyExcel.class).name()==null) {
                    continue;
                }
            }*/
        } catch (Exception e) {
            throw new RuntimeException("创建对象异常");
        }
        return obj;
    }

    /**
     * 获取class的 不包括父类的
     *
     * @param clazz
     * @return
     */
    public static Field[] getClassFields(Class<?> clazz) {
        List<Field> list = new ArrayList<Field>();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            list.add(fields[i]);
        }
        return list.toArray(fields);
    }

    /**
     * 获取列值得属性值
     *
     * @param titleMap
     * @param excelImportVo
     * @return
     */
    private static Map<Integer, Field> getTitleFileMap(Map<Integer, String> titleMap, ExcelImportVo excelImportVo)
            throws Exception {
        if (null == titleMap || titleMap.size() == 0) {
            throw new Exception("method getTitleFileMap excel titleMap is null");
        }
        Map<Integer, Field> titleFileMap = new LinkedHashMap<Integer, Field>();
        Class<?> pojoClass = excelImportVo.getPojoClass();
        Field[] fields = pojoClass.getDeclaredFields();
        Map<String, Field> hashMap = new HashMap<>();
        for (Field field : fields) {
            field.setAccessible(true);
            ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
            if (annotation == null) {
                continue;
            }
            String key = replaceGeneral(annotation.name(), excelImportVo.getPlaceholderMap());
            hashMap.put(key, field);
        }
        for (Integer key : titleMap.keySet()) {
            String value = titleMap.get(key);
            if (hashMap.containsKey(value)) {
                titleFileMap.put(key, hashMap.get(value));
            }
        }
        return titleFileMap;
    }

    /**
     * 通用替换占位
     *
     * @param str
     * @param placeholderMap
     * @return
     */
    private static String replaceGeneral(String str, Map<String, Object> placeholderMap) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        return replaceWithMap(str.replace(" ", ""), placeholderMap);
    }

    /**
     * 反射给属性赋值
     *
     * @param field
     * @param cell
     */
    private static void saveFieldValue(Field field, Cell cell, Object object) throws Exception {
        field.setAccessible(true);
        //属性导入注解解析(时间格式化)
        Object stringCellValue = importAnnotationParse(field, cell);
        if (null == stringCellValue) {
            return;
        }
        String type = field.getType().toString();
        if (type.endsWith("String") && null != stringCellValue) {
            field.set(object, stringCellValue); // 给属性设值
        } else if (type.endsWith("int") || type.endsWith("Integer") && null != stringCellValue) {
            field.set(object, Integer.valueOf((String) stringCellValue));
                 /*   try {
                        field.set(object, Integer.valueOf(stringCellValue));
                    } catch (Exception e) {
                        //格式错误的时候,就用double,然后获取Int值
                        field.set(object, Double.valueOf(stringCellValue).intValue());
                    }*/
        } else if (type.endsWith("char") && null != stringCellValue) {
            field.set(object, ((String) stringCellValue).charAt(0));
        } else if (type.endsWith("BigDecimal") && null != stringCellValue) {
            field.set(object, new BigDecimal((String) stringCellValue));
        } else if (type.endsWith("List") && null != stringCellValue) {
            // 给属性设值
            field.set(object, new ArrayList<>().add(stringCellValue));
        } else if (type.endsWith("Long") && null != stringCellValue) {
            field.set(object, Long.valueOf((String) stringCellValue));
                  /*  try {
                        field.set(object,Long.valueOf(stringCellValue));
                    } catch (Exception e) {
                        //格式错误的时候,就用double,然后获取Int值
                        field.set(object,Double.valueOf(stringCellValue).longValue());
                    }*/
        } else if ((type.endsWith("Float") || type.endsWith("float")) && null != stringCellValue) {
            field.set(object, Float.valueOf((String) stringCellValue));
        } else if ((type.endsWith("Boolean") || type.endsWith("boolean")) && null != stringCellValue) {
            field.set(object, Boolean.valueOf((String) stringCellValue));
        } else if ((type.endsWith("Double") || type.endsWith("double")) && null != stringCellValue) {
            field.set(object, Double.valueOf((String) stringCellValue));
        } else if ((type.endsWith("Date") || type.endsWith("Date")) && null != stringCellValue) {
            //具体包下类型自己实现
            field.set(object, stringCellValue);
        } else {
            field.set(object, stringCellValue);
        }

    }

    /**
     * excel 导入解析注解属性值
     *
     * @param field
     * @param cell
     * @return
     */
    private static Object importAnnotationParse(Field field, Cell cell) throws Exception {
        ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
        boolean importIsMust = annotation.importIsMust();
        if (importIsMust) {
            if (null == cell) {
                throw new Exception("类属性: " + field.getName() + " 注解: ExcelProperty 下属性 importIsMust 默认为 true ");
            }
        } else {
            if (null == cell) {
                return null;
            }
        }
        //设置单元格类型为String
        cell.setCellType(CellType.STRING);
        Object stringCellValue = cell.getStringCellValue();
        String[] importReplace = annotation.importReplace();
        if (importReplace != null && importReplace.length > 0) {
            stringCellValue = replaceValue(importReplace, (String) stringCellValue);
        }
        //导入后缀解析
        String importSuffix = annotation.importSuffix();
        if (StringUtils.isNotEmpty(importSuffix)) {
            stringCellValue = stringCellValue + importSuffix;
        }
        String importFormat = annotation.importFormat();
        if (StringUtils.isNotEmpty(importFormat)) {
            SimpleDateFormat format = new SimpleDateFormat(importFormat);
            try {
                stringCellValue = format.parse((String) stringCellValue);
            } catch (ParseException e) {
                log.error("导入excel时间解析异常 异常数据:{},error:{}", stringCellValue, JsonUtil.toJsonString(e));
                throw e;
            }
        }
        return stringCellValue;
    }

    /**
     * 响应
     *
     * @param response
     * @param work
     * @throws IOException
     */
    private static void responseWrite(HttpServletResponse response, Workbook work) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        work.write(outputStream);
        try {
            //有得版本会生成临时文件，自己不会删除，
            ((SXSSFWorkbook) work).dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
        outputStream.close();
    }

    /**
     * 设置格式
     *
     * @param response
     * @param name
     * @throws UnsupportedEncodingException
     */
    private static void responseContentType(HttpServletResponse response, String name)
            throws UnsupportedEncodingException {
        /* String filename = URLEncoder.encode(fileName, "utf-8");*/
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode(name, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + SUFFIX_XLSX);
            /*    //浏览器下载
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);
        response.flushBuffer();*/
    }

    /**
     * 替换字符串占位符, 字符串中使用{key}表示占位符
     *
     * @param sourceString 需要匹配的字符串，示例："名字:{name},年龄:{age},学校:{school}";
     * @param param        参数集,Map类型
     * @return
     */
    private static String replaceWithMap(String sourceString, Map<String, Object> param) {
        if (StringUtils.isEmpty(sourceString) || MapUtils.isEmpty(param)) {
            return sourceString;
        }
        String targetString = sourceString;
        matcher = pattern.matcher(sourceString);
        while (matcher.find()) {
            try {
                String key = matcher.group();
                String keyclone = key.substring(1, key.length() - 1).trim();
                Object value = param.get(keyclone);
                if (value != null) {
                    targetString = targetString.replace(key, value.toString());
                }
            } catch (Exception e) {
                throw new RuntimeException("String formatter failed", e);
            }
        }
        return targetString;
    }

}
