package com.devpeer.calories.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;

@Slf4j
public class Jackson {
    private static ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static String toJsonUnsafe(Object obj) {
        try {
            StringWriter writer = new StringWriter();
            objectMapper.writeValue(writer, obj);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
