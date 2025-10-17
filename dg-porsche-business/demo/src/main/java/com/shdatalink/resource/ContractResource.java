package com.shdatalink.resource;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shdatalink.entity.Contract;
import com.shdatalink.service.ContractService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.*;
import lombok.Data;
import org.jboss.resteasy.reactive.RestQuery;

import java.math.BigDecimal;
import java.time.LocalDate;

@Path("/contract")
public class ContractResource {

    @Inject
    ContractService contractService;

    @GET
    @Path("/pageQuery")
    public IPage<Contract> pageQuery() {
        return contractService.pageQuery();
    }

    @GET
    @Path("/pageQuery/lambda")
    public IPage<Contract> pageQueryLambda() {
        return contractService.pageQueryLambda();
    }

    @GET
    @Path("/getById")
    public Contract getById(@QueryParam("id") Long id) {
        return contractService.getById(id);
    }

    @POST
    @Path("/save")
    public void save(@Valid ContractSaveReq req) {
        contractService.saveContract(req);
    }

    @DELETE
    @Path("/delete")
    public void delete(@RestQuery("id") Long id) {
        contractService.remove(null);
    }

    @DELETE
    @Path("/delete/all")
    public void deleteAll() {
        contractService.remove(null);
    }

    @Data
    public static class ContractSaveReq {

        private Long id;

        /**
         * 合同编号
         */
        @NotBlank
        private String contractNo;

        /**
         * 合同类型：ONLINE-在线合同，OFFLINE-线下补录
         */
        @NotBlank
        private String contractType;

        /**
         * 一级品类(煤炭，钢材、农产品的等)
         */
        private Long firstCategoryId;

        /**
         * 二级品类(例如动力煤，焦炭等)
         */
        private Long secondCategoryId;

        /**
         * 合同状态
         */
        private String status;

        /**
         * 买方企业统一社会编码
         */
        private String buyerUscc;

        /**
         * 买方企业名称
         */
        private String buyerName;

        /**
         * 卖方企业统一社会编码
         */
        private String sellerUscc;

        /**
         * 卖方企业名称
         */
        private String sellerName;

        /**
         * 签订日期
         */
        private LocalDate signDate;

        /**
         * 基准价格
         */
        private BigDecimal unitPrice;

        /**
         * 数量
         */
        private BigDecimal quantity;

        /**
         * 执行开始日期
         */
        private LocalDate startDate;

        /**
         * 执行截止日期
         */
        private LocalDate endDate;
    }
}
