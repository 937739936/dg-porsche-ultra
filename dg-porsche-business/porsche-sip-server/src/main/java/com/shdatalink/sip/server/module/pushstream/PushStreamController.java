package com.shdatalink.sip.server.module.pushstream;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shdatalink.sip.server.module.pushstream.service.PushStreamService;
import com.shdatalink.sip.server.module.pushstream.vo.PushStreamPageResp;
import com.shdatalink.sip.server.module.pushstream.vo.PushStreamResp;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 推流管理
 */
@Path("admin/pushStream")
public class PushStreamController {

    @Inject
    PushStreamService pushStreamService;

    /**
     * 推流列表
     */
    @Path("page")
    @GET
    public IPage<PushStreamPageResp> page(@QueryParam("page") @DefaultValue("1") Integer page, @QueryParam("pageSize") @DefaultValue("10") Integer pageSize) {
        return pushStreamService.page(page, pageSize);
    }

    /**
     * 推流详情
     */
    @Path("detail")
    @GET
    public List<PushStreamResp> detail(@QueryParam("streamId") @NotBlank String streamId) {
        if (StringUtils.isBlank(streamId)) {
            return List.of();
        }
        return pushStreamService.detail(streamId);
    }

    /**
     * 关闭推流
     */
    @Path("closeStream")
    @GET
    public Boolean closeStream(@QueryParam("streamId") @NotBlank String streamId) {
        if (StringUtils.isBlank(streamId)) {
            return false;
        }
        return pushStreamService.closeStream(streamId);
    }
}
