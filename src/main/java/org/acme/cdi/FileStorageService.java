package org.acme.cdi;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.cdi.aop.Logged;

@ApplicationScoped
public class FileStorageService {

    private String storagePath;

    // Bean 初始化后执行（依赖注入完成后）
    @PostConstruct
    public void init() {
        // 初始化逻辑：创建存储目录、加载配置等
        storagePath = "/var/quarkus/storage";
        System.out.println("FileStorageService initialized. Storage path: " + storagePath);
    }

    // Bean 销毁前执行（应用关闭时）
    @PreDestroy
    public void destroy() {
        // 清理逻辑：关闭文件流、释放资源等
        System.out.println("FileStorageService destroying. Cleaning up resources...");
    }

    @Logged
    public String getStoragePath() {
        return storagePath;
    }
}