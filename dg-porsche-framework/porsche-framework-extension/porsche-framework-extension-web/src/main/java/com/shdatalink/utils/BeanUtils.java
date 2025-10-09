package com.shdatalink.utils;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;

import java.util.Set;

/**
 * 用于从CDI容器获取Bean
 * <p>
 * 类似Spring getBean()的工具类，
 */
public class BeanUtils {

    /**
     * 根据类型获取Bean实例（适用于单例或唯一实现类）
     * 类似 Spring 的 getBean(Class<T> clazz)
     */
    public static <T> T getBean(Class<T> clazz) {
        // 获取CDI容器的Bean管理器
        BeanManager beanManager = CDI.current().getBeanManager();

        // 查找指定类型的所有Bean
        Set<Bean<?>> beans = beanManager.getBeans(clazz);
        if (beans.isEmpty()) {
            throw new IllegalArgumentException("未找到类型为 " + clazz.getName() + " 的Bean");
        }

        // 获取第一个匹配的Bean
        Bean<?> bean = beanManager.resolve(beans);

        // 创建Bean的上下文并获取实例
        CreationalContext<?> ctx = beanManager.createCreationalContext(bean);
        @SuppressWarnings("unchecked")
        T instance = (T) beanManager.getReference(bean, clazz, ctx);

        return instance;
    }
}
