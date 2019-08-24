package com.devpeer.calories.core;

import com.devpeer.calories.core.query.QueryFilter;
import com.devpeer.calories.core.query.QueryFilterParser;
import org.junit.Test;

import static org.junit.Assert.*;

public class QueryFilterParserTests {

    @Test
    public void testEqFilter() {
        String eqFilter = "someField eq someValue";
        QueryFilter queryFilter = QueryFilterParser.parse(eqFilter);
        assertNotNull(queryFilter.getChainOperations());
        assertTrue(queryFilter.getChainOperations().isEmpty());
        assertEquals("someField", queryFilter.getKey());
        assertEquals("someValue", queryFilter.getValue());
        assertEquals(QueryFilter.Operator.EQ, queryFilter.getOperator());
    }

}
