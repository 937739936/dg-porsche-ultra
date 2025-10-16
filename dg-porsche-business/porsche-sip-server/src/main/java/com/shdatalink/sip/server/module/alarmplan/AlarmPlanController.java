package com.shdatalink.sip.server.module.alarmplan;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.json.utils.JsonUtil;
import com.shdatalink.sip.server.common.dto.PageParam;
import com.shdatalink.sip.server.module.alarmplan.convert.AlarmPlanConvert;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmMethodEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmPriorityEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.AlarmTypeEnum;
import com.shdatalink.sip.server.module.alarmplan.enums.EventTypeEnum;
import com.shdatalink.sip.server.module.alarmplan.service.AlarmPlanChannelRelService;
import com.shdatalink.sip.server.module.alarmplan.service.AlarmPlanService;
import com.shdatalink.sip.server.module.alarmplan.service.AlarmRecordService;
import com.shdatalink.sip.server.module.alarmplan.vo.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import java.util.List;

/**
 * 预警管理
 */
@Path("admin/alarm")
public class AlarmPlanController {

    @Inject
   AlarmRecordService alarmRecordService;
    @Inject
   AlarmPlanService alarmPlanService;
    @Inject
   AlarmPlanChannelRelService alarmPlanChannelRelService;
    @Inject
    AlarmPlanConvert alarmPlanConvert;
    /**
     * 报警记录列表
     */
    @Path("record/page")
    @GET
    public IPage<AlarmRecordPageResp> recordPage(AlarmRecordPageReq pageParam) {
        return alarmRecordService.getPage(pageParam);
    }

    /**
     * 删除报警记录
     */
    @Path("record/batchDelete")
    @POST
    public Boolean batchDeleteRecord(IdListVO idListVO) {
        return alarmRecordService.batchDeleteRecord(idListVO.getIdList());
    }

    /**
     * 保存编辑报警订阅
     */
    @Path("plan/save")
    @POST
    public Boolean saveOrUpdate(@Valid AlarmPlanReq alarmPlanReq) {
        return alarmPlanService.saveOrUpdate(alarmPlanReq);
    }

    /**
     * 报警预案列表
     */
    @Path("plan/page")
    @GET
    public IPage<AlarmPlanPageResp> planPage(PageParam pageParam) {
        return alarmPlanService.getPage(pageParam).convert(item -> {
            AlarmPlanPageResp resp = new AlarmPlanPageResp();
            resp.setId(item.getId());
            resp.setName(item.getName());
            resp.setAlarmPriorities(JsonUtil.stringToEnumList(item.getAlarmPriorities(), AlarmPriorityEnum.class));
            resp.setAlarmMethods(JsonUtil.stringToEnumList(item.getAlarmMethods(), AlarmMethodEnum.class));
            resp.setAlarmTypes(JsonUtil.stringToEnumList(item.getAlarmTypes(), AlarmTypeEnum.class));
            resp.setEventTypes(JsonUtil.stringToEnumList(item.getEventTypes(), EventTypeEnum.class));
            resp.setStatus(item.getStatus());
            resp.setCreatedTime(item.getCreatedTime());
            resp.setLastModifiedTime(item.getLastModifiedTime());
            return resp;
        });
    }

    /**
     * 删除报警预案
     */
    @Path("plan/delete")
    @GET
    public Boolean deletePlan(Integer planId) {
        alarmPlanService.delete(planId);
        return true;
    }

    /**
     * 报警预案id所关联的所有通道
     */
    @Path("plan/getChannels")
    @GET
    public List<AlarmPlanChannelResp> getChannels(Integer planId) {
        return alarmPlanChannelRelService.getChannels(planId).stream().map(item -> alarmPlanConvert.convert(item)).toList();
    }

    /**
     * 保存报警预案和通道关系
     */
    @Path("savePlanChannels")
    @POST
    public Boolean savePlanChannels(@Valid AlarmPlanChannelSaveReq saveReq) {
        alarmPlanService.getOptById(saveReq.getPlanId()).orElseThrow(() -> new BizException("未找到对应的报警预案信息"));
        alarmPlanChannelRelService.savePlanChannels(saveReq);
        return true;
    }


}
