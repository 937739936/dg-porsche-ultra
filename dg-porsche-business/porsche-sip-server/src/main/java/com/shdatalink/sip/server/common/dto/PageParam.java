package com.shdatalink.sip.server.common.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

@Data
public class PageParam {
    private long page = 1;
    private long pageSize = 20;

    public <T> Page<T> toPage() {
        return new Page<>(page, pageSize);
    }
}
