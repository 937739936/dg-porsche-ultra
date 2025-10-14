package com.shdatalink.sip.service.module.config.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shdatalink.sip.service.module.config.entity.Config;
import com.shdatalink.sip.service.module.config.enums.ConfigTypesEnum;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ConfigMapper extends BaseMapper<Config> {

    @Select("select * from t_config where type = #{type}")
    List<Config> selectByType(ConfigTypesEnum type);

    @Delete("delete from t_config where type  = #{type}")
    void deleteByType(ConfigTypesEnum type);
}
