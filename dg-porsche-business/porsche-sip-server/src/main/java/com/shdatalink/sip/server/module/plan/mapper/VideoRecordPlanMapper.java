package com.shdatalink.sip.server.module.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shdatalink.sip.server.module.plan.entity.VideoRecordDevice;
import com.shdatalink.sip.server.module.plan.entity.VideoRecordPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoRecordPlanMapper extends BaseMapper<VideoRecordPlan> {

    @Select("select * from t_video_record_plan where ${weekDay} & #{flag} != 0 and enabled = 1")
    List<VideoRecordPlan> selectByWeekDayAndFlag(String weekDay, int flag);

    List<VideoRecordDevice> selectDeviceByWeekDayAndFlag(String weekDay, String deviceId, String channelId, int flag);
}
