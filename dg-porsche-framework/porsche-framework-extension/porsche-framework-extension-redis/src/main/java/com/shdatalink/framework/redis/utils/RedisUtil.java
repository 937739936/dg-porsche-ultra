package com.shdatalink.framework.redis.utils;

import com.shdatalink.framework.json.utils.JsonUtil;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.hash.HashCommands;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.list.ListCommands;
import io.quarkus.redis.datasource.set.SetCommands;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Redis 工具类，
 * <p>
 * 提供 Redis 字符串类型的常用操作
 */
@ApplicationScoped
public class RedisUtil {

    private final KeyCommands<String> keyCommands;
    private final ValueCommands<String, String> valueCommands;
    private final ListCommands<String, String> listCommands;
    private final HashCommands<String, String, String> hashCommands;

    /**
     * 注入 RedisDataSource 并获取 ValueCommands 实例
     *
     * @param ds Redis数据源
     */
    @Inject
    public RedisUtil(RedisDataSource ds) {
        this.keyCommands = ds.key(String.class);
        this.valueCommands = ds.value(String.class, String.class);
        this.listCommands = ds.list(String.class, String.class);
        this.hashCommands = ds.hash(String.class, String.class, String.class);
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


    /**
     * 删除指定键的值
     */
    public boolean delete(String key) {
        return keyCommands.del(key) == 1;
    }


    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     */
    public <T> void setList(final String key, final List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        dataList.forEach(data -> {
            String jsonValue = JsonUtil.toJsonString(data);
            listCommands.rpush(key, jsonValue);
        });
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getList(final String key, Class<T> clazz) {
        List<String> list = listCommands.lrange(key, 0, -1);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream()
                .map(json -> JsonUtil.parseObject(json, clazz))
                .collect(Collectors.toList());
    }

    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public <T> void setMapValue(final String key, final String hKey, final T value) {
        hashCommands.hset(key, hKey, JsonUtil.toJsonString(value));
    }

    /**
     * 缓存Map
     *
     * @param key     缓存的键值
     * @param dataMap 缓存键值对应的数据
     */
    public <T> void setMap(final String key, final Map<String, T> dataMap) {
        if (MapUtils.isEmpty(dataMap)) {
            return;
        }
        for (Map.Entry<String, T> entry : dataMap.entrySet()) {
            setMapValue(key, entry.getKey(), entry.getValue());
        }
    }


    /**
     * 获得缓存的Map
     *
     * @param key 缓存的键值
     */
    public <T> Map<String, T> getMap(final String key, Class<T> clazz) {
        Map<String, String> stringMap = hashCommands.hgetall(key);
        Map<String, T> resultMap = new HashMap<>(stringMap.size());
        for (Map.Entry<String, String> entry : stringMap.entrySet()) {
            resultMap.put(entry.getKey(), JsonUtil.parseObject(entry.getValue(), clazz));
        }
        return resultMap;
    }

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getMapValue(final String key, final String hKey, Class<T> clazz) {
        String value = hashCommands.hget(key, hKey);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return JsonUtil.parseObject(value, clazz);
    }

    /**
     * 删除Hash中的某条数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     */
    public void deleteMapValue(final String key, final String hKey) {
        hashCommands.hdel(key, hKey);
    }

}
