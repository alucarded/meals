package com.devpeer.calories.core.query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class QueryFilterTypesResolver {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static Map<Class<?>, TypeResolver> typeResolverRegistry = new HashMap<>();

    static {
        // Default resolvers
        typeResolverRegistry.put(Integer.class, Integer::parseInt);
        typeResolverRegistry.put(Double.class, Double::parseDouble);
        typeResolverRegistry.put(String.class, (str) -> str);
        typeResolverRegistry.put(LocalDate.class, (str) -> LocalDate.parse(str, DATE_FORMATTER));
        typeResolverRegistry.put(LocalTime.class, (str) -> LocalTime.parse(str, TIME_FORMATTER));
    }

    private FieldTypeResolver fieldTypeResolver;

    public QueryFilterTypesResolver(Class<?> klass) {
        fieldTypeResolver = new FieldTypeResolver(klass);
    }

    public QueryFilter resolveTypes(QueryFilter queryFilter) {
        if (queryFilter.getKey() != null && queryFilter.getValue() != null) {
            Class<?> fieldType = fieldTypeResolver.getClassForField(queryFilter.getKey());
            Object value = typeResolverRegistry.get(fieldType).resolve((String) queryFilter.getValue());
            queryFilter.setValue(value);
        }

        queryFilter.getChainOperations().forEach(this::resolveTypes);

        return queryFilter;
    }
}
