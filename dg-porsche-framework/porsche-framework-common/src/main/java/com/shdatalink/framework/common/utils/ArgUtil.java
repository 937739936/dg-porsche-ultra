package com.shdatalink.framework.common.utils;

import com.shdatalink.framework.common.exception.BaseResultCodeEnum;
import com.shdatalink.framework.common.exception.BizException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 校验工具类
 */
public class ArgUtil {


    public static <T> T judgeNull(T target, String message) {
        return Optional.ofNullable(target).orElseThrow(() -> new BizException(message));
    }

    public static <T> T judgeNull(T target, String message, Object... args) {
        return Optional.ofNullable(target).orElseThrow(() -> new BizException(String.format(message, args)));
    }

    public static <T> T judgeNull(T target, BaseResultCodeEnum errorCode) {
        return Optional.ofNullable(target).orElseThrow(() -> new BizException(errorCode));
    }

    public static <T> T judgeNull(Optional<T> targetOption, BaseResultCodeEnum errorCode) {
        return targetOption.orElseThrow(() -> new BizException(errorCode));
    }

    public static  <T> void judgeNonNull(T target, String message) {
        if (Objects.isNull(target)) {
            return;
        }
        throw new BizException(message);
    }

    public static <E, T extends Collection<E>> T judgeEmpty(T target, String message) {
        if (CollectionUtils.isEmpty(target)) {
            throw new BizException(message);
        }
        return target;
    }

    public static <E, T extends Collection<E>> T judgeEmpty(T target, String message, Object... args) {
        if (CollectionUtils.isEmpty(target)) {
            throw new BizException(String.format(message, args));
        }
        return target;
    }

    public static <K, V, T extends Map<K, V>> T judgeEmpty(T target, String message, Object... args) {
        if (MapUtils.isEmpty(target)) {
            throw new BizException(String.format(message, args));
        }
        return target;
    }

    public static <E, T extends Collection<E>> T judgeNotEmpty(T target, String message) {
        if (CollectionUtils.isNotEmpty(target)) {
            throw new BizException(message);
        }
        return target;
    }

    public static <E, T extends Collection<E>> T judgeNotEmpty(T target, String message, Object... args) {
        if (CollectionUtils.isNotEmpty(target)) {
            throw new BizException(String.format(message, args));
        }
        return target;
    }

    public static String judgeBlank(String target, String message) {
        return Optional.ofNullable(target)
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new BizException(message));
    }

    public static String judgeBlank(String target, String message, Object... args) {
        return Optional.ofNullable(target)
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new BizException(String.format(message, args)));
    }

    public static String judgeNotBlank(String target, String message, Object... args) {
        if (StringUtils.isNotBlank(target)) {
            throw new BizException(String.format(message, args));
        }
        return target;
    }

    public static void isFalseWithThrow(Boolean flag, String message) {
        if (!flag) {
            throw new BizException(message);
        }
    }

    public static void isFalseWithThrow(Boolean flag, String message, Object... args) {
        if (!flag) {
            throw new BizException(String.format(message, args));
        }
    }

    public static void isFalseWithThrow(Boolean flag, BaseResultCodeEnum errorCode) {
        if (!flag) {
            throw new BizException(errorCode);
        }
    }

    public static void isTrueWithThrow(Boolean flag, String message) {
        if (flag) {
            throw new BizException(message);
        }
    }

    public static void isTrueWithThrow(Boolean flag, BaseResultCodeEnum bizErrorCode) {
        if (flag) {
            throw new BizException(bizErrorCode.getMessage());
        }
    }

    public static void isTrueWithThrow(Boolean flag, String message, Object... args) {
        if (flag) {
            throw new BizException(String.format(message, args));
        }
    }

    public static boolean isPatternMatch(Object target, String pattern) {
        if (Objects.isNull(target) ) {
            return false;
        }

        String str = String.valueOf(target);
        if (StringUtils.isBlank(str)) {
            return false;
        }

        if (StringUtils.isBlank(pattern)) {
            return false;
        }

        return Pattern.compile(pattern).matcher(str).matches();
    }

    public static Boolean isStartWithStr(String target, String str) {
        if (StringUtils.isBlank(target) || StringUtils.isBlank(str)) {
            return false;
        }
        return target.startsWith(str);
    }

}
