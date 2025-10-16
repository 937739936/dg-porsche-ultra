package com.shdatalink.sip.server.module.plan.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shdatalink.sip.server.module.plan.service.VideoRecordPlanService;
import com.shdatalink.sip.server.module.plan.vo.VideoPlanPageParam;
import com.shdatalink.sip.server.module.plan.vo.VideoRecordPlanDetailVO;
import com.shdatalink.sip.server.module.plan.vo.VideoRecordPlanPage;
import com.shdatalink.sip.server.module.plan.vo.VideoRecordPlanSaveParam;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import lombok.RequiredArgsConstructor;

/**
 * 录像回放/录像计划
 */
@RequiredArgsConstructor
@Path("admin/video/record/plan")
public class VideoRecordPlanController {
    @Inject
    VideoRecordPlanService videoRecordPlanService;

    /**
     * 录像计划列表
     *
     * @param param
     * @return
     */
    @Path("page")
    @GET
    public IPage<VideoRecordPlanPage> page(VideoPlanPageParam param) {
        return videoRecordPlanService.getPage(param);
    }

    /**
     * 启用/禁用
     *
     * @param id 计划id
     */
    @Path("enableSwitch")
    @GET
    public boolean enableSwitch(@QueryParam("id") Integer id, @QueryParam("enabled") Boolean enabled) {
        return videoRecordPlanService.enableSwitch(id, enabled);
    }

    /**
     * 删除
     *
     * @param id 计划id
     */
    @Path("delete")
    @DELETE
    public boolean delete(@QueryParam("id") Integer id) {
        return videoRecordPlanService.removeById(id);
    }

    /**
     * 保存
     */
    @Path("save")
    @POST
    public boolean save(@Valid VideoRecordPlanSaveParam param) {
        return videoRecordPlanService.save(param);
    }

    /**
     * 详情
     */
    @Path("detail")
    @GET
    public VideoRecordPlanDetailVO detail(@QueryParam("id") Integer id) {
        return videoRecordPlanService.detail(id);
    }
}
