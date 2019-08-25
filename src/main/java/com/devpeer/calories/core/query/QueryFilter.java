package com.devpeer.calories.core.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
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

    public QueryFilter(QueryFilter queryFilter) {
        key = queryFilter.getKey();
        value = queryFilter.getValue();
        chainOperations = queryFilter.getChainOperations();
        operator = queryFilter.getOperator();
    }

    @Nullable private String key;
    @Nullable private Object value;
    private List<QueryFilter> chainOperations = new ArrayList<>();
    private Operator operator;

    public void addChainOperation(QueryFilter queryFilter) {
        chainOperations.add(queryFilter);
    }
}
