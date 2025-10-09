package com.shdatalink.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.shdatalink.handler.InjectionMetaObjectHandler;
import io.quarkus.arc.DefaultBean;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.sql.DataSource;


@ApplicationScoped
public class MyBatisPlusConfig {


    /**
     * 配置MyBatis-Plus插件
     */
    @Produces
    @DefaultBean
    @ApplicationScoped
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        // 防全表更新与删除插件
        mybatisPlusInterceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        // 乐观锁插件
        mybatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 分页插件(如果配置多个插件, 切记分页最后添加)
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        // 可选：限制单页最大条数（防止恶意请求）
        paginationInterceptor.setMaxLimit(5L);
        mybatisPlusInterceptor.addInnerInterceptor(paginationInterceptor);
        return mybatisPlusInterceptor;
    }



}
