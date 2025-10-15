package com.shdatalink.sip.server.module.device;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import java.util.List;
import java.util.Objects;

/**
 * 录像回放/录像回放
 */
@Path("admin/device/record")
public class DeviceRecordController {


    /**
     * 录像回放-时间线
     *
     * @param deviceId  设备id
     * @param channelId 通道id
     * @param date      日期
     * @param type      local:本地 remote:远端
     * @return
     */
    @GET
    @Path("timeline")
    public List<Objects> timeline(@QueryParam("deviceId") String deviceId
            , @QueryParam("channelId") String channelId
            , @QueryParam("date") String date
            , @QueryParam("type") String type
    ) {
//        if ("local".equals(type)) {
//            return videoRecordService.timeline(deviceId, channelId, date);
//        } else {
//            return videoRecordRemoteService.timeline(deviceId, channelId, date);
//        }
        return null;
    }

}
