package com.shdatalink.sip.server.module.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shdatalink.sip.server.module.device.entity.Device;
import com.shdatalink.sip.server.module.device.vo.DevicePageParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

@Mapper
public interface DeviceMapper extends BaseMapper<Device> {
    Page<Device> getPage(Page<Device> page, @Param("param") DevicePageParam param);

    List<Device> selectByDeviceIdList(@Param("deviceId") Collection<String> deviceId);

    @Select("select * from t_device where enable = 1 and register_time is not null")
    List<Device> selectPreviewList();

    @Select("select * from t_device where device_id = #{deviceId} ")
    Device selectByDeviceId(String deviceId);

    @Select("select d.* from t_device d left join t_device_channel dc on dc.device_id = d.device_id where dc.channel_id = #{channelId}")
    Device selectByChannelId(String channelId);
}
