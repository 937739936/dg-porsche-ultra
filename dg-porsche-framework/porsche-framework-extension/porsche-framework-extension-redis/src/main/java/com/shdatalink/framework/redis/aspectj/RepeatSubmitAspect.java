package com.shdatalink.framework.redis.aspectj;

import com.shdatalink.framework.common.exception.BizException;
import com.shdatalink.framework.json.utils.JsonUtil;
import com.shdatalink.framework.redis.annotation.RepeatSubmit;
import com.shdatalink.framework.redis.utils.RedisUtil;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;


/**
 * 防止重复提交
 *
 * @author huyulong
 */
@Slf4j
@RepeatSubmit
@Interceptor
public class RepeatSubmitAspect {

    @Context
    HttpHeaders httpHeaders;

    @Context
    UriInfo uriInfo;

    @Inject
    RedisUtil redisUtil;

    /**
     * 防重提交 redis key
     */
    private static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * KEY缓存
     */
    private static final ThreadLocal<String> KEY_CACHE = new ThreadLocal<>();


    @AroundInvoke
    public Object execute(InvocationContext context) throws Exception {
        log.info("invoked {}", context.getMethod().getName());

        // 获取目标方法上的 @RepeatSubmit 注解
        RepeatSubmit repeatSubmit = context.getInterceptorBinding(RepeatSubmit.class);

        // 如果注解不为0 则使用注解数值
        long interval = repeatSubmit.timeUnit().toMillis(repeatSubmit.interval());
        if (interval < 1000) {
            throw new BizException("重复提交间隔时间不能小于'1'秒");
        }

        // 请求地址（作为存放cache的key值）
        String url = uriInfo.getAbsolutePath().toString();
        // 方法参数
        String nowParams = argsArrayToString(context.getParameters());
        // token
        String token = StringUtils.trimToEmpty(httpHeaders.getHeaderString(repeatSubmit.tokenName()));
        // 唯一值
        String submitKey = DigestUtils.md5Hex(token + ":" + nowParams);
        // 缓存唯一标识
        String cacheRepeatKey = REPEAT_SUBMIT_KEY + url + submitKey;

        // 判断是否重复提交
        boolean exists = redisUtil.exists(cacheRepeatKey);
        if (exists) {
            throw new BizException(repeatSubmit.message());
        }

        Object result;
        try {
            // 在缓存中记录本次信息
            redisUtil.setEx(cacheRepeatKey, "", Duration.ofMillis(interval));
            KEY_CACHE.set(cacheRepeatKey);

            // 执行方法
            result = context.proceed();

            // 处理完请求后执行
            this.doAfterReturning();
        } catch (Exception e) {
            doAfterThrowing();
            throw e;
        }
        return result;
    }

    /**
     * 处理完请求后执行
     */
    private void doAfterReturning() {
        redisUtil.del(KEY_CACHE.get());
        KEY_CACHE.remove();
    }

    /**
     * 发生异常操后作
     */
    private void doAfterThrowing() {
        redisUtil.del(KEY_CACHE.get());
        KEY_CACHE.remove();
    }


    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray) {
        StringJoiner params = new StringJoiner(" ");
        if (paramsArray == null) {
            return params.toString();
        }
        for (Object o : paramsArray) {
            if (Objects.nonNull(o) && !isFilterObject(o)) {
                params.add(JsonUtil.toJsonString(o));
            }
        }
        return params.toString();
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return File.class.isAssignableFrom(clazz.getComponentType());
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof File;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.values()) {
                return value instanceof File;
            }
        }
        return o instanceof File;
    }


}
