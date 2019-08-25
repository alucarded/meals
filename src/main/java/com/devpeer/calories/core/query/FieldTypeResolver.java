package com.devpeer.calories.core.query;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class FieldTypeResolver {

    private Map<String, Class<?>> classForField;

    public FieldTypeResolver(Class<?> klass) {
        classForField = Arrays.stream(klass.getDeclaredFields())
                .map(field -> new AbstractMap.SimpleEntry<String, Class<?>>(field.getName(), field.getType()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    Class<?> getClassForField(String fieldName) {
        return classForField.get(fieldName);
    }
}
