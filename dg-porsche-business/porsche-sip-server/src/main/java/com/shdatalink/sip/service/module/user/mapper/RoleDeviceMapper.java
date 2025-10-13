package com.shdatalink.sip.service.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shdatalink.sip.service.module.user.entity.RoleDevice;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleDeviceMapper extends BaseMapper<RoleDevice> {

    @Select("select device_id from t_role_device where role_id = #{roleId} ")
    List<String> selectByRoleId(Integer roleId);

    @Delete("delete from t_role_device where role_id = #{roleId}")
    void deleteByRoleId(Integer roleId);
}
