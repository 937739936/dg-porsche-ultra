package com.shdatalink.sip.server.module.common;

import com.shdatalink.framework.common.annotation.IgnoredResultWrapper;
import com.shdatalink.framework.common.exception.BizException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


/**
 * 文件访问接口
 */
@Path(value = "/admin")
@Slf4j
@ApplicationScoped
public class ResourceController {

    @ConfigProperty(name = "attach.upload.path")
    String uploadPath;

    @GET
    @Path("/files/{path:.*}")
    @IgnoredResultWrapper
    public Response getFile(@PathParam("path") String requestPath, String fileName) {
        String requestUri = "files" + File.separator + requestPath;

        log.info("文件请求拦截路径:{}", requestPath);
        String uri = uploadPath + File.separator + requestUri;
        File file = new File(uri);
        if (!file.exists()) {
            throw new BizException("附件不存在！");
        }
        fileName = (fileName == null || fileName.isEmpty()) ? file.getName() : fileName;
        try {
            return Response.ok(new FileInputStream(file))
                    .header("Content-Type", "application/octet-stream")
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .header("Content-Length", String.valueOf(file.length()))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
