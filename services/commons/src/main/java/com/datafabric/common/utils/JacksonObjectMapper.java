package com.datafabric.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonObjectMapper {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return mapper.convertValue(fromValue, toValueType);
    }

    public static void updateValue(Object toValue, Object fromValue) {
        try {
            mapper.readerForUpdating(toValue).readValue(mapper.writeValueAsString(fromValue));
        } catch (Exception e) {
            throw new RuntimeException("Failed to update object", e);
        }
    }
}