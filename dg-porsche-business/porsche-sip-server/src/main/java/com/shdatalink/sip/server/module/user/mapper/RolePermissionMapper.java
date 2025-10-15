package com.shdatalink.sip.server.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shdatalink.sip.server.module.user.entity.RolePermission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    @Select("select * from t_role_permission where role_id  = #{roleId} ")
    List<RolePermission> selectByRoleId(Integer roleId);

    @Delete("delete from t_role_permission where role_id  =#{roleId} ")
    void deleteByRoleId(Integer roleId);
}
