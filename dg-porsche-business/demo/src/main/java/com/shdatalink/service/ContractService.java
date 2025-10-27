package com.shdatalink.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shdatalink.entity.Contract;
import com.shdatalink.mapper.ContractMapper;
import com.shdatalink.resource.ContractResource;
import io.quarkiverse.mybatis.plus.extension.service.impl.ServiceImpl;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@RegisterForReflection(lambdaCapturingTypes = "com.shdatalink.service.ContractService",
        targets = {SerializedLambda.class, SFunction.class},
        serialization = true)
@Slf4j
@ApplicationScoped
public class ContractService extends ServiceImpl<ContractMapper, Contract> {


    public IPage<Contract> pageQueryLambda() {
//        LambdaQueryWrapper<Contract> queryWrapper = new LambdaQueryWrapper<Contract>()
//                .eq(Contract::getStatus, "执行中");
//        Page<Contract> page = page(new Page<>(1, 10), queryWrapper);
        return baseMapper.queryPage(new Page<>(1, 10));
//        return page;
    }

    public IPage<Contract> pageQuery() {
        QueryWrapper<Contract> queryWrapper = new QueryWrapper<Contract>()
                .eq("status", "执行中");
        Page<Contract> page = page(new Page<>(1, 10), queryWrapper);
        return page;
    }

    @Transactional(rollbackOn = Exception.class)
    public boolean saveContract(ContractResource.ContractSaveReq req) {
        Contract contract = new Contract();
        if (req.getId() != null) {
            contract = getById(req.getId());
        } else {
            contract = new Contract();
        }
        contract.setContractNo(req.getContractNo());
        contract.setContractType(req.getContractType());
        contract.setFirstCategoryId(req.getFirstCategoryId());
        contract.setSecondCategoryId(req.getSecondCategoryId());
        contract.setStatus("执行中");
        contract.setBuyerUscc(req.getBuyerUscc());
        contract.setBuyerName(req.getBuyerName());
        contract.setSellerUscc(req.getSellerUscc());
        contract.setSellerName(req.getSellerName());
        contract.setSignDate(LocalDate.now());
        contract.setUnitPrice(req.getUnitPrice());
        contract.setQuantity(req.getQuantity());
        contract.setStartDate(LocalDate.now());
        contract.setEndDate(LocalDate.now());

        return saveOrUpdate(contract);
    }


}
