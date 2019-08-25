package com.devpeer.calories.core.query;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class MongoCriteriaBuilder<T> {

    private final Class<T> klass;

    public MongoCriteriaBuilder(Class<T> klass) {
        this.klass = klass;
    }

    public static <T> MongoCriteriaBuilder<T> create(Class<T> klass) {
        return new MongoCriteriaBuilder<>(klass);
    }

    public Criteria build(QueryFilter queryFilter) {
        // TODO: resolve types ?
        return parseQueryFilter(queryFilter);
    }

    private Criteria parseQueryFilter(QueryFilter queryFilter) {
        switch (queryFilter.getOperator()) {
            case AND:
                return and(queryFilter.getChainOperations());
            case OR:
                return or(queryFilter.getChainOperations());
            case EQ:
                return eq(queryFilter.getKey(), queryFilter.getValue());
            case NE:
                return ne(queryFilter.getKey(), queryFilter.getValue());
            case GT:
                return gt(queryFilter.getKey(), queryFilter.getValue());
            case LT:
                return lt(queryFilter.getKey(), queryFilter.getValue());
            default:
                throw new UnsupportedOperationException();
        }
    }

    private Criteria and(List<QueryFilter> queryFilters) {
        return new Criteria().andOperator(queryFilters.stream()
                .map(this::parseQueryFilter)
                .toArray(Criteria[]::new));
    }

    private Criteria or(List<QueryFilter> queryFilters) {
        return new Criteria().orOperator(queryFilters.stream()
                .map(this::parseQueryFilter)
                .toArray(Criteria[]::new));
    }

    private Criteria gt(String field, Object value) {
        return new Criteria(field).gt(value);
    }

    private Criteria lt(String field, Object value) {
        return new Criteria(field).lt(value);
    }

    private Criteria eq(String field, Object value) {
        return new Criteria(field).is(value);
    }

    private Criteria ne(String field, Object value) {
        return new Criteria(field).ne(value);
    }
}
