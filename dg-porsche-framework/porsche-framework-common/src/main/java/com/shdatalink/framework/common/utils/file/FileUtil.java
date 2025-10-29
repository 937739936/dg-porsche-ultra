package com.shdatalink.framework.common.utils.file;

import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.framework.common.model.FileInfo;
import com.shdatalink.framework.common.utils.ArgUtil;
import com.shdatalink.framework.common.utils.DateUtil;
import com.shdatalink.framework.common.utils.IdUtil;
import jakarta.ws.rs.core.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 文件上传工具类
 *
 * @author huyulong
 */
@Slf4j
public class FileUtil {

    /**
     * 允许上传的文件类型
     */
    public static final Set<String> DEFAULT_ALLOWED_EXTENSION = Set.of(
            // 图片
            "bmp", "gif", "jpg", "jpeg", "png",
            // word excel powerpoint
            "doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt",
            // 压缩文件
            "rar", "zip", "gz", "bz2",
            // 视频格式
            "mp4", "avi", "rmvb",
            // pdf
            "pdf"
    );

    /**
     * 文件上传(批量)
     *
     * @param uploadDir   文件上传目录
     * @param fileUploads 文件上传对象
     * @return 返回上传成功的文件名
     */
    public static List<FileInfo> upload(String uploadDir, List<FileUpload> fileUploads) {
        if (CollectionUtils.isEmpty(fileUploads)) {
            throw new BizException("fileUploads is null");
        }

        return fileUploads.parallelStream()
                .map(fileUpload -> upload(uploadDir, fileUpload))
                .collect(Collectors.toList());
    }

    /**
     * 文件上传
     *
     * @param uploadDir  文件上传目录
     * @param fileUpload 文件上传对象
     * @return 返回上传成功的文件名
     */
    @SneakyThrows
    public static FileInfo upload(String uploadDir, FileUpload fileUpload) {
        if (StringUtils.isBlank(uploadDir)) {
            throw new BizException("uploadDir can't be blank.");
        }
        if (fileUpload == null) {
            throw new BizException("fileUpload is null");
        }
        // 1、文件上传校验
        assertAllowed(fileUpload);

        // 文件类型
        String extension = getExtension(fileUpload.fileName());

        // 2、创建上传目录
        String uploadPath = createUploadPath(uploadDir);

        // 3. 构建目标保存路径（上传目录 + 文件名）
        Path targetPath = Path.of(uploadPath, createFileName(extension));

        // 4. 复制临时文件到目标路径（覆盖已存在的文件）
        Files.copy(fileUpload.uploadedFile(), targetPath);

        // 5、封装文件信息
        return FileInfo.builder()
                .name(fileUpload.fileName())
                .size(fileUpload.size())
                .suffix(extension)
                .path(targetPath.toString())
                .uploadTime(LocalDateTime.now())
                .build();
    }


    /**
     * 生成存储文件名称
     */
    public static String createFileName(String extension) {
        return String.format("%s.%s", IdUtil.simpleUUID(), extension);
    }

    /**
     * 获取文件名的后缀
     *
     * @param fileName 文件名称
     * @return 后缀名
     */
    public static String getExtension(String fileName) {
        return FilenameUtils.getExtension(fileName).toLowerCase();
    }


    /**
     * 创建上传目录。
     *
     * @param uploadDir 上传目录的路径
     */
    public static String createUploadPath(String uploadDir) throws IOException {
        Path uploadPath = Path.of(uploadDir + File.separator + DateUtil.getDatePath());
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        return uploadPath.toString();
    }

    /**
     * 文件校验
     */
    public static void assertAllowed(FileUpload fileUpload) {
        // 文件类型校验
        ArgUtil.isFalseWithThrow(DEFAULT_ALLOWED_EXTENSION.contains(getExtension(fileUpload.fileName())), "不允许上传的文件类型");
    }

    /**
     * 导出excel
     *
     * @param filePath     文件路径
     * @param realFileName 真实文件名
     */
    public static Response download(String filePath, String realFileName) {
        // 创建文件对象
        File file = new File(filePath);
        // 校验文件是否存在
        if (!file.exists() || !file.isFile()) {
            throw new BizException("文件不存在");
        }

        // 构建Response对象并设置header
        Response.ResponseBuilder responseBuilder = Response.ok(file);
        setAttachmentResponseHeader(realFileName, responseBuilder);

        // 构建并返回响应
        return responseBuilder.build();
    }


    /**
     * 下载文件名重新编码
     *
     * @param realFileName 真实文件名
     */
    public static void setAttachmentResponseHeader(String realFileName, Response.ResponseBuilder responseBuilder) {
        String percentEncodedFileName = encode(realFileName);
        String contentDispositionValue = "attachment; filename=%s;filename*=utf-8''%s".formatted(percentEncodedFileName, percentEncodedFileName);
        responseBuilder.header("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
        responseBuilder.header("Content-disposition", contentDispositionValue);
        responseBuilder.header("download-filename", percentEncodedFileName);
    }

    /**
     * 对文件名进行encode
     */
    public static String encode(String realFileName) {
        String encode = URLEncoder.encode(realFileName, StandardCharsets.UTF_8);
        return encode.replaceAll("\\+", "%20");
    }
}
