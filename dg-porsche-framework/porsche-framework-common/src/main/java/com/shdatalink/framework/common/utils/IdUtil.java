package com.shdatalink.framework.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * ID生成器工具类
 */
public class IdUtil {
    /**
     * 获取随机UUID
     *
     * @return 随机UUID
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线
     *
     * @return 简化的UUID，去掉了横线
     */
    public static String simpleUUID() {
        return StringUtils.replaceChars(UUID.randomUUID().toString(), "-", "");
    }


    public static void main(String[] args) {
        System.out.println(randomUUID());
        System.out.println(simpleUUID());
    }
}
