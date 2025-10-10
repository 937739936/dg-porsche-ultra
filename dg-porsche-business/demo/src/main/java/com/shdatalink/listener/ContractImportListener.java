package com.shdatalink.listener;


import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.shdatalink.core.ExcelListener;
import com.shdatalink.core.ExcelResult;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.resource.ExcelResource;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 系统用户自定义导入
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class ContractImportListener extends AnalysisEventListener<ExcelResource.ContractImportVo> implements ExcelListener<ExcelResource.ContractImportVo> {

    private int successNum = 0;
    private int failureNum = 0;
    private final StringBuilder successMsg = new StringBuilder();
    private final StringBuilder failureMsg = new StringBuilder();

    @Override
    public ExcelResult<ExcelResource.ContractImportVo> getExcelResult() {
        return new ExcelResult<>() {

            @Override
            public String getAnalysis() {
                if (failureNum > 0) {
                    failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
                    throw new BizException(failureMsg.toString());
                } else {
                    successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
                }
                return successMsg.toString();
            }

            @Override
            public List<ExcelResource.ContractImportVo> getList() {
                return null;
            }

            @Override
            public List<String> getErrorList() {
                return null;
            }
        };
    }

    @Override
    public void invoke(ExcelResource.ContractImportVo contractImportVo, AnalysisContext analysisContext) {
        System.out.println("解析到一条数据: " + contractImportVo.getContractNo());
        successNum++;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
