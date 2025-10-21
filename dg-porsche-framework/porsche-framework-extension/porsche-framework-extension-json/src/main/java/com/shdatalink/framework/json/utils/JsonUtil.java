package com.shdatalink.framework.json.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.shdatalink.framework.common.utils.QuarkusUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JSON 工具类
 */
@Slf4j
public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = QuarkusUtil.getBean(ObjectMapper.class);


    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    /**
     * 将对象转换为JSON格式的字符串
     *
     * @param object 要转换的对象
     * @return JSON格式的字符串，如果对象为null，则返回null
     * @throws RuntimeException 如果转换过程中发生JSON处理异常，则抛出运行时异常
     */
    public static String toJsonString(Object object) {
        if (Objects.isNull(object)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将JSON格式的字符串转换为指定类型的对象
     *
     * @param text  JSON格式的字符串
     * @param clazz 要转换的目标对象类型
     * @param <T>   目标对象的泛型类型
     * @return 转换后的对象，如果字符串为空则返回null
     * @throws RuntimeException 如果转换过程中发生IO异常，则抛出运行时异常
     */
    public static <T> T parseObject(String text, Class<T> clazz) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        try {
            // 针对原生镜像环境，提前校验类是否可实例化（辅助排查问题）
            checkClassForDeserialization(clazz);
            return OBJECT_MAPPER.readValue(text, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 辅助检查类是否适合反序列化（非必须，但可提前暴露问题）
     */
    private static <T> void checkClassForDeserialization(Class<T> clazz) {
        // 检查是否为接口或抽象类（无法直接实例化）
        if (clazz.isInterface() || java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
            throw new IllegalArgumentException("目标类 " + clazz.getName() + " 是接口或抽象类，无法直接反序列化");
        }
        // 检查是否有可访问的构造方法（简单校验，实际反序列化可能依赖更多条件）
        try {
            clazz.getDeclaredConstructor(); // 检查无参构造是否存在
        } catch (NoSuchMethodException e) {
            // 仅作为警告提示，因为可能通过 @JsonCreator 注解使用有参构造
            log.warn("警告：类 " + clazz.getName() + " 未找到无参构造方法，若未使用 @JsonCreator 可能导致反序列化失败");
        }
    }

    /**
     * 将JSON格式的字符串转换为指定类型的对象，支持复杂类型
     *
     * @param text          JSON格式的字符串
     * @param typeReference 指定类型的TypeReference对象
     * @param <T>           目标对象的泛型类型
     * @return 转换后的对象，如果字符串为空则返回null
     * @throws RuntimeException 如果转换过程中发生IO异常，则抛出运行时异常
     */
    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        try {
            Type type = extractTypeFromTypeReference(typeReference);
            JavaType javaType = TypeFactory.defaultInstance().constructType(type);
            return OBJECT_MAPPER.readValue(text, javaType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从 TypeReference 中提取 Type 信息
     */
    private static Type extractTypeFromTypeReference(TypeReference<?> typeReference) {
        try {
            // TypeReference 通常通过匿名内部类实现，我们可以获取其泛型信息
            Type genericSuperclass = typeReference.getClass().getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) genericSuperclass;
                Type[] actualTypeArguments = paramType.getActualTypeArguments();
                if (actualTypeArguments.length > 0) {
                    return actualTypeArguments[0];
                }
            }
            throw new IllegalArgumentException("Invalid TypeReference: " + typeReference);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot extract type from TypeReference", e);
        }
    }

    /**
     * 将JSON格式的字符串转换为指定类型对象的列表
     *
     * @param text  JSON格式的字符串
     * @param clazz 要转换的目标对象类型
     * @param <T>   目标对象的泛型类型
     * @return 转换后的对象的列表，如果字符串为空则返回空列表
     * @throws RuntimeException 如果转换过程中发生IO异常，则抛出运行时异常
     */
    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        if (StringUtils.isEmpty(text)) {
            return new ArrayList<>();
        }
        try {
            return OBJECT_MAPPER.readValue(text, OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将字符串转为 List<Enum>
     *
     * @param input   JSON数组字符串，如 ["EQUIPMENT_ALARM","DEVICE_ERROR"]
     * @param enumCls 枚举类型的Class
     * @param <E>     枚举类型
     * @return List<E> 枚举列表
     */
    public static <E extends Enum<E>> List<E> stringToEnumList(String input, Class<E> enumCls) {
        if (input == null || input.isBlank()) {
            return Collections.emptyList();
        }

        try {
            // 先解析成字符串列表
            List<String> strList = getObjectMapper().readValue(input, new TypeReference<>() {
            });

            return strList.stream()
                    .map(s -> normalizeAndConvert(s, enumCls)) // 转换为枚举
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new IllegalArgumentException("无法解析字符串为枚举列表: " + input, e);
        }
    }

    /**
     * 容错转换：忽略大小写、空格、下划线
     */
    private static <E extends Enum<E>> E normalizeAndConvert(String value, Class<E> enumCls) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim()
                .toUpperCase(Locale.ROOT)
                .replace(" ", "_");
        return Enum.valueOf(enumCls, normalized);
    }
}
