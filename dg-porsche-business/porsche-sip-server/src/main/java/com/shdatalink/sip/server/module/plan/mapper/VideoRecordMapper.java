package com.shdatalink.sip.server.module.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shdatalink.sip.server.module.plan.entity.VideoRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper
public interface VideoRecordMapper extends BaseMapper<VideoRecord> {
    @Select("select * from t_video_record where device_id = #{deviceId} and channel_id = #{channelId} and date = #{date} order by start_time")
    List<VideoRecord> getVideoRecords(@Param("deviceId") String deviceId, @Param("channelId") String channelId, @Param("date") LocalDate date);

    @Select("select * from t_video_record where device_id = #{deviceId} and channel_id = #{channelId} and date = #{date} and file_path = #{mp4}")
    Optional<VideoRecord> selectMp4File(String deviceId, String channelId, LocalDate date, String mp4);
}
