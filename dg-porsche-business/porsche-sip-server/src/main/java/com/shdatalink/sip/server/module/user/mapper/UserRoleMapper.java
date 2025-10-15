package com.shdatalink.sip.server.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shdatalink.sip.server.module.user.entity.UserRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    @Delete("delete from t_user_role where user_id = #{id} ")
    void deleteByUserId(Integer id);

    @Select("select * from t_user_role where user_id = #{id}")
    List<UserRole> selectByUserId(Integer id);
}
