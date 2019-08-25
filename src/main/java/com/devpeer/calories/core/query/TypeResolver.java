package com.devpeer.calories.core.query;

@FunctionalInterface
public interface TypeResolver {
    Object resolve(String value);
}
