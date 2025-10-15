package com.shdatalink.sip.server.common.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.ws.rs.QueryParam;
import lombok.Data;

@Data
public class PageParamWithGet {

    @QueryParam(value = "page")
    private long page = 1;
    @QueryParam(value = "pageSize")
    private long pageSize = 20;

    public <T> Page<T> toPage() {
        return new Page<>(page, pageSize);
    }
}
