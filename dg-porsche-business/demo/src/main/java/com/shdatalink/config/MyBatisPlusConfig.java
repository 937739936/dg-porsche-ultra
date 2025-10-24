package com.shdatalink.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.apache.ibatis.session.SqlSessionFactory;


@ApplicationScoped
public class MyBatisPlusConfig {

    // 注入 MyBatis 自动初始化的 SqlSessionFactory
    @Inject
    SqlSessionFactory sqlSessionFactory;

    @Inject
    CustomTenantHandler customTenantHandler;

    /**
     * 配置MyBatis-Plus插件（监听应用启动事件，在启动时注册插件）
     * <p>
     * 分页插件quarkus扩展已经默认添加，此处请勿重复添加
     */
    void onStartup(@Observes StartupEvent event) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 防全表更新与删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        TenantLineInnerInterceptor tenantInterceptor = new TenantLineInnerInterceptor();
        tenantInterceptor.setTenantLineHandler(customTenantHandler);
        interceptor.addInnerInterceptor(tenantInterceptor);
        // 将拦截器注册到 MyBatis 的全局配置中
        sqlSessionFactory.getConfiguration().addInterceptor(interceptor);
    }


}
