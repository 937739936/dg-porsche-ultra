package com.shdatalink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shdatalink.entity.Contract;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ContractMapper extends BaseMapper<Contract> {

    Page<Contract> queryPage(Page<Contract> page);

}
