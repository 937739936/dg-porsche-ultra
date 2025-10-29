package com.shdatalink.resource;


import com.shdatalink.framework.common.annotation.IgnoredResultWrapper;
import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.framework.common.model.FileInfo;
import com.shdatalink.framework.common.utils.file.FileUtil;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Path("/file")
public class FileResource {


    private static final String baseUploadPath = "/Users/huyulong/project/quarkus/upload";

    @IgnoredResultWrapper
    @Path("/upload")
    @POST
    public FileInfo upload(@RestForm("file") FileUpload fileUpload) throws Exception {
        return FileUtil.upload(baseUploadPath, fileUpload);
    }


    /**
     * 文件下载
     */
    @GET
    @Path("/download/v2")
    public Response download(@QueryParam("path") String path, @QueryParam("fileName") String fileName) throws Exception {
        String filePath = baseUploadPath + File.separator + path;
        return FileUtil.download(filePath, fileName);
    }

    /**
     * 文件下载
     */
    @GET
    @Path("/download")
    public Response download() {
        String fileName = "1760672851873_测试 (1).xlsx";
        String filePath = "/Users/huyulong/Downloads/" + fileName;
        File file = new File(filePath);
        // 2. 校验文件是否存在
        if (!file.exists() || !file.isFile()) {
            throw new BizException("文件不存在");
        }
        //对输出的文件名进行编码，防止下载的中文文件名乱码
        String encodeFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        return Response.ok(file)
                .header("content-disposition", "attachment; filename=\"" + encodeFileName + "\"")
                .header("Content-Length", file.length())
                .build();
    }

}


