package com.shdatalink.framework.web.config.converter;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 自定义的ParamConverterProvider，
 * 用于处理Java时间类型（LocalDate, LocalTime, LocalDateTime）与字符串之间的转换。
 *
 * @author huyulong
 */
@Provider
public class JavaTimeParamConverterProvider implements ParamConverterProvider {

    // 定义各类型的默认格式（根据前端实际传递的格式调整）
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @SuppressWarnings("unchecked")
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        // 处理LocalDate
        if (rawType.equals(LocalDate.class)) {
            return (ParamConverter<T>) new ParamConverter<LocalDate>() {
                @Override
                public LocalDate fromString(String value) {
                    return value == null || value.isEmpty() ? null : LocalDate.parse(value, DATE_FORMATTER);
                }

                @Override
                public String toString(LocalDate value) {
                    return value == null ? null : DATE_FORMATTER.format(value);
                }
            };
        }
        // 处理LocalTime
        else if (rawType.equals(LocalTime.class)) {
            return (ParamConverter<T>) new ParamConverter<LocalTime>() {
                @Override
                public LocalTime fromString(String value) {
                    return value == null || value.isEmpty() ? null : LocalTime.parse(value, TIME_FORMATTER);
                }

                @Override
                public String toString(LocalTime value) {
                    return value == null ? null : TIME_FORMATTER.format(value);
                }
            };
        }
        // 处理LocalDateTime
        else if (rawType.equals(LocalDateTime.class)) {
            return (ParamConverter<T>) new ParamConverter<LocalDateTime>() {
                @Override
                public LocalDateTime fromString(String value) {
                    return value == null || value.isEmpty() ? null : LocalDateTime.parse(value, DATE_TIME_FORMATTER);
                }

                @Override
                public String toString(LocalDateTime value) {
                    return value == null ? null : DATE_TIME_FORMATTER.format(value);
                }
            };
        }
        return null;
    }
}
