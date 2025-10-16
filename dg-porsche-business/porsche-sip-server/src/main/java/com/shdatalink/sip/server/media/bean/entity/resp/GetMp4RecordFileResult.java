package com.shdatalink.sip.server.media.bean.entity.resp;

import lombok.Data;

import java.util.List;

@Data
public class GetMp4RecordFileResult {

    private List<String> paths;

    private String rootPath;

}
