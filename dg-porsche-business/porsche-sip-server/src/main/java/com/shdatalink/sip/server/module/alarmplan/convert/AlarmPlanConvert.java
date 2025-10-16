package com.shdatalink.sip.server.module.alarmplan.convert;

import com.shdatalink.framework.common.config.QuarkusMappingConfig;
import com.shdatalink.sip.server.module.alarmplan.entity.AlarmPlanChannelRel;
import com.shdatalink.sip.server.module.alarmplan.entity.Subscribe;
import com.shdatalink.sip.server.module.alarmplan.vo.AlarmPlanChannelResp;
import com.shdatalink.sip.server.module.alarmplan.vo.SubscribeReq;
import com.shdatalink.sip.server.module.alarmplan.vo.SubscribeResp;
import org.mapstruct.Mapper;

@Mapper(config = QuarkusMappingConfig.class)
public interface AlarmPlanConvert {
    AlarmPlanChannelResp convert(AlarmPlanChannelRel rel);

    SubscribeResp convert(Subscribe req);
}
