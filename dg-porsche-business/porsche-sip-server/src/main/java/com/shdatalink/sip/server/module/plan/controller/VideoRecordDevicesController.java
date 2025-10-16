package com.shdatalink.sip.server.module.plan.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shdatalink.sip.server.common.dto.PageParam;
import com.shdatalink.sip.server.module.plan.service.VideoRecordDeviceService;
import com.shdatalink.sip.server.module.plan.service.VideoRecordService;
import com.shdatalink.sip.server.module.plan.vo.VideoDeviceBindList;
import com.shdatalink.sip.server.module.plan.vo.VideoRecordDevicePage;
import com.shdatalink.sip.server.module.plan.vo.VideoRecordDeviceParam;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;

import java.util.List;

/**
 * 录像回放/录像记录列表
 */
@Path("admin/video/record/devices")
public class VideoRecordDevicesController {
    @Inject
    VideoRecordDeviceService videoRecordDeviceService;
    @Inject
    VideoRecordService videoRecordService;

    /**
     * 关联设备
     */
    @Path("add")
    @POST
    public boolean add(@Valid VideoRecordDeviceParam param) {
        videoRecordDeviceService.add(param);
        return true;
    }

    /**
     * 查询设备绑定列表
     * @param planId
     * @return
     */
    @Path("bindList")
    @GET
    public List<VideoDeviceBindList> bindList(@QueryParam("planId") Integer planId) {
        return videoRecordDeviceService.getBindList(planId);
    }

    /**
     * 分页
     */
    @Path("page")
    @GET
    public IPage<VideoRecordDevicePage> getPage(PageParam param) {
        return videoRecordDeviceService.getPage(param);
    }

    /**
     * 删除
     * @param channelId 设备通道ID
     */
    @Path("delete")
    @DELETE
    public boolean delete(@QueryParam("channelId") String channelId) {
        return videoRecordService.deleteByChannelId(channelId);
    }
}
