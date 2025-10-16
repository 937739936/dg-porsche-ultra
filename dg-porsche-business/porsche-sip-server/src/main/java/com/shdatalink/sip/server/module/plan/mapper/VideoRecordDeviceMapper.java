package com.shdatalink.sip.server.module.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shdatalink.sip.server.module.plan.entity.VideoRecordDevice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoRecordDeviceMapper extends BaseMapper<VideoRecordDevice> {
    @Select("select * from t_video_record_device where plan_id = #{planId} and device_id = #{deviceId} and channel_id = #{channelId}")
    VideoRecordDevice selectByPlanIdAndDeviceIdAndChannelId(Integer planId, String deviceId, String channelId);

    @Select("select * from t_video_record_device where plan_id = #{planId}")
    List<VideoRecordDevice> selectByPlanId(Integer id);

    @Select("delete from t_video_record_device where plan_id = #{planId}")
    void deleteByPlanId(Integer id);
}
