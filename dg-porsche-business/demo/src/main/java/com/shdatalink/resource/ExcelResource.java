package com.shdatalink.resource;


import com.shdatalink.excel.annotation.ExcelProperty;
import com.shdatalink.excel.utils.ExcelUtil;
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ContractImportVo {

        @ExcelProperty("合同编号")
        private String contractNo;
    }

}
