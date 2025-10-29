package com.shdatalink.resource;


import cn.idev.excel.annotation.ExcelProperty;
import com.shdatalink.framework.common.annotation.IgnoredResultWrapper;
import com.shdatalink.framework.excel.model.SheetData;
import com.shdatalink.framework.excel.utils.ExcelUtil;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.reactive.RestForm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Path("/excel")
public class ExcelResource {


    @Path("/import")
    @POST
    public List<ContractImportVo> importExcel(@RestForm File file) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(file);
        return ExcelUtil.importExcel(fileInputStream, ContractImportVo.class);
    }

    @Path("/export")
    @GET
    public Response exportExcel() throws IOException, IllegalAccessException {
        List<ContractImportVo> dataList = new ArrayList<>();
        dataList.add(new ContractImportVo("001"));
        dataList.add(new ContractImportVo("002"));
        dataList.add(new ContractImportVo("003"));
        return ExcelUtil.exportExcel(dataList, "测试", ContractImportVo.class);
    }

    @Path("/export/multi")
    @GET
    public Response exportExcelMulti() {
        List<SheetData> sheetList = new ArrayList<>();


        List<ContractImportVo> dataList = new ArrayList<>();
        dataList.add(new ContractImportVo("001"));
        dataList.add(new ContractImportVo("002"));
        dataList.add(new ContractImportVo("003"));

        List<InvoiceVO> dataList2 = new ArrayList<>();
        dataList2.add(new InvoiceVO("2025001"));
        dataList2.add(new InvoiceVO("2025002"));
        dataList2.add(new InvoiceVO("2025003"));

        sheetList.add(new SheetData(1, "合同", ContractImportVo.class, dataList));
        sheetList.add(new SheetData(2, "发票", InvoiceVO.class, dataList2));

        return ExcelUtil.exportExcel("多sheet导出", sheetList);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ContractImportVo {

        @ExcelProperty("合同编号")
        private String contractNo;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InvoiceVO {

        @ExcelProperty("发票号码")
        private String invoiceNo;
    }

}
