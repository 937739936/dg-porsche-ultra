package com.shdatalink.sip.server.config.web;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Provider
public class LocalDateTimeParamConverterProvider implements ParamConverterProvider {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @SuppressWarnings("unchecked")
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType == LocalDateTime.class) {
            return (ParamConverter<T>) new ParamConverter<LocalDateTime>() {
                @Override
                public LocalDateTime fromString(String value) {
                    return LocalDateTime.parse(value, FORMATTER);
                }

                @Override
                public String toString(LocalDateTime value) {
                    return value.format(FORMATTER);
                }
            };
        }
        return null;
    }
}
