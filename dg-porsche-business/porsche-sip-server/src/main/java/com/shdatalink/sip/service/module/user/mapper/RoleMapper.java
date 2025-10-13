package com.shdatalink.sip.service.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shdatalink.sip.service.module.user.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    @Select("select count(*) from t_role where name = #{name} and id != #{id}")
    int checkByName(Integer id, String name);
}
