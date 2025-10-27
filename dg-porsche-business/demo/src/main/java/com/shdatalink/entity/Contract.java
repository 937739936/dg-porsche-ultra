package com.shdatalink.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_contract")
public class Contract {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 合同类型：ONLINE-在线合同，OFFLINE-线下补录
     */
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
