package com.shdatalink.sip.server.module.plan.convert;

import com.shdatalink.framework.common.config.QuarkusMappingConfig;
import com.shdatalink.sip.server.module.plan.entity.VideoRecordDevice;
import com.shdatalink.sip.server.module.plan.entity.VideoRecordPlan;
import com.shdatalink.sip.server.module.plan.vo.VideoDeviceBindList;
import com.shdatalink.sip.server.module.plan.vo.VideoRecordDevicePage;
import com.shdatalink.sip.server.module.plan.vo.VideoRecordPlanPage;
import org.mapstruct.Mapper;

@Mapper(config = QuarkusMappingConfig.class)
public interface VideoRecordConvert {
    VideoRecordDevicePage toPage(VideoRecordDevice videoRecordDevice);

    VideoDeviceBindList to(VideoRecordDevice videoRecordDevice);

    VideoRecordPlanPage to(VideoRecordPlan plan);
}
