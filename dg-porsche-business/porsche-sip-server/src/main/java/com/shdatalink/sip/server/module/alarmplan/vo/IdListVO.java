package com.shdatalink.sip.server.module.alarmplan.vo;

import jakarta.ws.rs.QueryParam;
import lombok.Data;

import java.util.List;

@Data
public class IdListVO {

    @QueryParam("idList")
    private List<Integer> idList;
}
