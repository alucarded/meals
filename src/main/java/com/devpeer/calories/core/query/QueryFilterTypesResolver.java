package com.devpeer.calories.core.query;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static com.devpeer.calories.core.Common.DATE_FORMATTER;
import static com.devpeer.calories.core.Common.TIME_FORMATTER;

@Slf4j
public class QueryFilterTypesResolver {

    private static Map<Class<?>, TypeResolver> typeResolverRegistry = new HashMap<>();

    static {
        // Default resolvers
        typeResolverRegistry.put(Integer.class, Integer::parseInt);
        typeResolverRegistry.put(Float.class, Double::parseDouble);
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
            TypeResolver typeResolver = typeResolverRegistry.get(fieldType);
            if (typeResolver != null) {
                Object value = typeResolver.resolve((String) queryFilter.getValue());
                queryFilter.setValue(value);
            } else {
                log.info("Missing type resolver for {}", fieldType.getSimpleName());
            }
        }

        queryFilter.getChainOperations().forEach(this::resolveTypes);

        return queryFilter;
    }
}
