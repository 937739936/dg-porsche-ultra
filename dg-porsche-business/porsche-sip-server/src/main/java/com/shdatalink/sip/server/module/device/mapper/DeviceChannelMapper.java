package com.shdatalink.sip.server.module.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shdatalink.sip.server.module.device.entity.DeviceChannel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Mapper
public interface DeviceChannelMapper extends BaseMapper<DeviceChannel> {
    @Select("select * from t_device_channel where device_id = #{deviceId} and channel_id = #{channelId} and enable = 1")
    DeviceChannel selectByDeviceIdAndChannelId(@Param("deviceId") String deviceId, @Param("channelId") String channelId);

    @Select("select * from t_device_channel where channel_id = #{channelId}")
    DeviceChannel selectByChannelId(@Param("channelId") String channelId);

    @Select("select * from t_device_channel where device_id = #{deviceId} ")
    List<DeviceChannel> selectByDeviceId(String deviceId);

    @Select("select * from t_device_channel where device_id = #{deviceId} and enable = 1 and register_time is not null")
    List<DeviceChannel> selectPreviewByDeviceId(String deviceId);

    @Select("select ifnull(CAST(max(RIGHT(channel_id, 5)) AS SIGNED), 0) from t_device_channel")
    int getMaxSerialNumber();

    @Select("select * from t_device_channel where device_id = #{deviceId} and channel_id = #{channelId}")
    Optional<DeviceChannel> selectOptByDeviceIdAndChannelId(String deviceId, String channelId);

    @Update("update t_device_channel set online = 0, leave_time = now() where device_id = #{deviceId} and register_time is not null and leave_time is null")
    void setDeviceOffline(String deviceId);

    @Select("<script>" +
            "select * from t_device_channel " +
            "where channel_id in " +
            "<foreach collection=\"channelIds\" item=\"item\" open=\"(\" separator=\",\" close=\")\">#{item}</foreach>" +
            "</script>")
    List<DeviceChannel> selectByChannelIds(Collection<String> channelIds);

//    ChannelBaseInfoDTO getBaseChannelInfo(@Param("id") Integer id);
}
