package com.devpeer.calories.core.query;

public class QueryFilterParseException extends RuntimeException {
    public QueryFilterParseException(Throwable cause) {
        super("Failed to parse query filter", cause);
    }
}
