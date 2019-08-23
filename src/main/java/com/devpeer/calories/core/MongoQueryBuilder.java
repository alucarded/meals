package com.devpeer.calories.core;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class MongoQueryBuilder {
    public static Query build(QueryFilter queryFilter) {
        // TODO:
        return Query.query(Criteria.where("id"));
    }
}
