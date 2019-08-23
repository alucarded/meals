package com.devpeer.calories.core;

public class QueryFilterParser {

    private QueryFilter queryFilter;

    public static QueryFilter parse(String filter) {
        QueryFilterParser queryFilterParser = new QueryFilterParser(filter);
        return queryFilterParser.getQueryFilter();
    }

    QueryFilterParser(String filter) {
        // TODO: parse
    }

    QueryFilter getQueryFilter() {
        return queryFilter;
    }
}
