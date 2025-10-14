package com.shdatalink.redis.utils;

import com.shdatalink.json.utils.JsonUtil;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Duration;
import java.util.Optional;

/**
 * Redis 工具类，
 * <p>
 * 提供 Redis 字符串类型的常用操作
 */
@ApplicationScoped
public class RedisUtil {

    private final ValueCommands<String, String> valueCommands;
    private final KeyCommands<String> keyCommands;

    /**
     * 注入 RedisDataSource 并获取 ValueCommands 实例
     *
     * @param ds Redis数据源
     */
    @Inject
    public RedisUtil(RedisDataSource ds) {
        this.valueCommands = ds.value(String.class, String.class);
        this.keyCommands = ds.key(String.class);
    }

    /**
     * 设置指定 key 的值
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, String value) {
        valueCommands.set(key, value);
    }

    /**
     * 设置指定key的对象值（自动序列化为JSON）
     *
     * @param key   键
     * @param value 要存储的对象
     */
    public <T> void set(String key, T value) {
        String jsonValue = JsonUtil.toJsonString(value);
        valueCommands.set(key, jsonValue);
    }

    /**
     * 设置指定key的对象值并指定过期时间
     *
     * @param key      键
     * @param value    要存储的对象
     * @param duration 过期时间
     */
    public void setEx(String key, String value, Duration duration) {
        valueCommands.setex(key, duration.getSeconds(), value);
    }

    /**
     * 设置指定key的对象值并指定过期时间
     *
     * @param key      键
     * @param value    要存储的对象
     * @param duration 过期时间
     */
    public <T> void setEx(String key, T value, Duration duration) {
        String jsonValue = JsonUtil.toJsonString(value);
        valueCommands.setex(key, duration.getSeconds(), jsonValue);
    }

    /**
     * 获取指定key的值
     *
     * @param key 键
     */
    public String get(String key) {
        return valueCommands.get(key);
    }

    /**
     * 获取指定key的对象值（自动反序列化为指定类型）
     *
     * @param key   键
     * @param clazz 目标对象类型
     * @return 反序列化后的对象，不存在则返回null
     */
    public <T> T get(String key, Class<T> clazz) {
        String jsonValue = valueCommands.get(key);
        if (jsonValue == null) {
            return null;
        }
        return JsonUtil.parseObject(jsonValue, clazz);
    }

    /**
     * 获取指定key的对象值，返回Optional类型
     *
     * @param key   键
     * @param clazz 目标对象类型
     * @return 包含对象的Optional，不存在则返回空Optional
     */
    public <T> Optional<T> getOpt(String key, Class<T> clazz) {
        return Optional.ofNullable(get(key, clazz));
    }

    /**
     * 删除指定key
     *
     * @param key 键
     */
    public void del(String key) {
        keyCommands.del(key);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     */
    public boolean exists(String key) {
        return keyCommands.exists(key);
    }


    /**
     * 设置key过期时间
     *
     * @param key 键
     */
    public boolean expire(String key, Duration duration) {
        return keyCommands.expire(key, duration);
    }

}
