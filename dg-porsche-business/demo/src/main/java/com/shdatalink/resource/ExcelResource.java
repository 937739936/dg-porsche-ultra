package com.shdatalink.resource;

import com.alibaba.excel.annotation.ExcelProperty;
import com.shdatalink.core.ExcelResult;
import com.shdatalink.entity.Contract;
import com.shdatalink.framework.common.annotation.IgnoredResultWrapper;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.listener.ContractImportListener;
import com.shdatalink.utils.ExcelUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.jboss.resteasy.reactive.RestForm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Path("/excel")
public class ExcelResource {


    @Path("/import")
    @POST
    public ExcelResult<ContractImportVo> importExcel(@RestForm File file) throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);
        return ExcelUtil.importExcel(fileInputStream, ContractImportVo.class, new ContractImportListener());
    }

    @Path("/export")
    @GET
    public Response exportExcel(@RestForm File file) {
        List<ContractImportVo> dataList = new ArrayList<>();
        dataList.add(new ContractImportVo("001"));
        dataList.add(new ContractImportVo("002"));
        dataList.add(new ContractImportVo("003"));
        return ExcelUtil.exportExcel(dataList, "测试导出", ContractImportVo.class);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ContractImportVo {

        @ExcelProperty("合同编号")
        private String contractNo;
    }

}
