package com.shdatalink.framework.common.utils;

import io.quarkus.arc.Arc;
import io.quarkus.arc.ArcContainer;
import io.quarkus.arc.InstanceHandle;
import io.quarkus.runtime.LaunchMode;


/**
 * Quarkus工具类
 * 提供获取Bean、检查Bean存在性等功能
 */
public class QuarkusUtil {

    private QuarkusUtil() {
        // 私有构造函数，防止实例化
    }

    /**
     * 获取Quarkus的Arc容器
     */
    public static ArcContainer getArcContainer() {
        return Arc.container();
    }

    /**
     * 如果容器包含一个与所给名称匹配的bean，则返回true
     */
    public static boolean containsBean(String name) {
        return getArcContainer().beanManager().getBeans(name).isEmpty();
    }

    /**
     * 获取指定名称的bean的类型
     */
    public static Class<?> getType(String name) {
        return getArcContainer().beanManager().getBeans(name)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No bean found with name: " + name))
                .getBeanClass();
    }

    /**
     * 获取指定类型的Bean实例
     */
    public static <T> T getBean(Class<T> type) {
        try (InstanceHandle<T> instance = getArcContainer().instance(type)) {
            return instance.get();
        } catch (Exception e) {
            throw new RuntimeException("No bean found for type: " + type.getName(), e);
        }
    }

    /**
     * 检查是否处于开发模式
     */
    public static boolean isDev() {
        return LaunchMode.current() == LaunchMode.DEVELOPMENT;
    }

    /**
     * 检查是否处于测试模式
     */
    public static boolean isTest() {
        return LaunchMode.current() == LaunchMode.TEST;
    }

    /**
     * 检查是否处于生产模式
     */
    public static boolean isProd() {
        return LaunchMode.current() == LaunchMode.NORMAL;
    }
}
