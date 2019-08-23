package com.devpeer.calories.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryFilter {
    public enum Operator {
        EQ,
        NE,
        LT,
        GT,
        AND,
        OR
    }

    @Nullable private String key;
    @Nullable private Object value;
    private List<QueryFilter> chainOperations;
    private Operator operator;
}
