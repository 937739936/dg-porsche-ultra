package com.shdatalink.sip.server.module.pushstream;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shdatalink.sip.server.module.pushstream.service.PushStreamService;
import com.shdatalink.sip.server.module.pushstream.vo.PushStreamPageResp;
import com.shdatalink.sip.server.module.pushstream.vo.PushStreamResp;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.reactive.RestQuery;

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
    public IPage<PushStreamPageResp> page(@RestQuery Integer page, @RestQuery Integer pageSize) {
        return pushStreamService.page(page, pageSize);
    }

    /**
     * 推流详情
     */
    @Path("detail")
    @GET
    public List<PushStreamResp> detail(@RestQuery String streamId) {
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
    public Boolean closeStream(@RestQuery String streamId) {
        if (StringUtils.isBlank(streamId)) {
            return false;
        }
        return pushStreamService.closeStream(streamId);
    }
}
