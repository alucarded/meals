package com.devpeer.calories.core;

import com.devpeer.calories.core.query.QueryFilter;
import com.devpeer.calories.core.query.QueryFilterParseException;
import com.devpeer.calories.core.query.QueryFilterParser;
import org.junit.Ignore;
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

    private void parseAndFilter(String andFilter) {
        QueryFilter queryFilter = QueryFilterParser.parse(andFilter);
        assertNotNull(queryFilter);
        assertNull(queryFilter.getKey());
        assertNull(queryFilter.getValue());
        assertNotNull(queryFilter.getOperator());
        assertEquals(QueryFilter.Operator.AND, queryFilter.getOperator());
        assertNotNull(queryFilter.getChainOperations());
        assertEquals(2, queryFilter.getChainOperations().size());

        QueryFilter firstFilter = queryFilter.getChainOperations().get(0);
        assertNotNull(firstFilter.getKey());
        assertEquals("someField1", firstFilter.getKey());
        assertNotNull(firstFilter.getOperator());
        assertEquals(QueryFilter.Operator.EQ, firstFilter.getOperator());
        assertNotNull(firstFilter.getValue());
        assertEquals("someValue1", firstFilter.getValue());
        assertNotNull(firstFilter.getChainOperations());
        assertTrue(firstFilter.getChainOperations().isEmpty());

        QueryFilter secondFilter = queryFilter.getChainOperations().get(1);
        assertNotNull(secondFilter.getKey());
        assertEquals("someField2", secondFilter.getKey());
        assertNotNull(secondFilter.getOperator());
        assertEquals(QueryFilter.Operator.NE, secondFilter.getOperator());
        assertNotNull(secondFilter.getValue());
        assertEquals("someValue2", secondFilter.getValue());
        assertNotNull(secondFilter.getChainOperations());
        assertTrue(secondFilter.getChainOperations().isEmpty());
    }

    @Test
    public void testAndFilter() {
        String andFilter = "(someField1 eq someValue1) AND (someField2 ne someValue2)";
        parseAndFilter(andFilter);
    }

    @Test
    public void testAndFilterAdditionalParenthesis() {
        String andFilter = "((someField1 eq someValue1) AND (someField2 ne someValue2))";
        parseAndFilter(andFilter);
    }

    // TODO: add more tests
    @Ignore
    @Test(expected = QueryFilterParseException.class)
    public void testAndFilterWrongParenthesis() {
        String andFilter = "(((someField1 eq someValue1) AND (someField2 ne someValue2))";
        parseAndFilter(andFilter);
    }

    @Test
    public void testManyAndFilter() {
        String andFilter = "(someField1 eq someValue1) AND (someField2 ne someValue2) AND (someField3 gt someValue3)";
        QueryFilter queryFilter = QueryFilterParser.parse(andFilter);
        assertNotNull(queryFilter);
        assertNull(queryFilter.getKey());
        assertNull(queryFilter.getValue());
        assertNotNull(queryFilter.getOperator());
        assertEquals(QueryFilter.Operator.AND, queryFilter.getOperator());
        assertNotNull(queryFilter.getChainOperations());
        assertEquals(3, queryFilter.getChainOperations().size());

        QueryFilter firstFilter = queryFilter.getChainOperations().get(0);
        assertNotNull(firstFilter.getKey());
        assertEquals("someField1", firstFilter.getKey());
        assertNotNull(firstFilter.getOperator());
        assertEquals(QueryFilter.Operator.EQ, firstFilter.getOperator());
        assertNotNull(firstFilter.getValue());
        assertEquals("someValue1", firstFilter.getValue());
        assertNotNull(firstFilter.getChainOperations());
        assertTrue(firstFilter.getChainOperations().isEmpty());

        QueryFilter secondFilter = queryFilter.getChainOperations().get(1);
        assertNotNull(secondFilter.getKey());
        assertEquals("someField2", secondFilter.getKey());
        assertNotNull(secondFilter.getOperator());
        assertEquals(QueryFilter.Operator.NE, secondFilter.getOperator());
        assertNotNull(secondFilter.getValue());
        assertEquals("someValue2", secondFilter.getValue());
        assertNotNull(secondFilter.getChainOperations());
        assertTrue(secondFilter.getChainOperations().isEmpty());

        QueryFilter thirdFilter = queryFilter.getChainOperations().get(2);
        assertNotNull(thirdFilter.getKey());
        assertEquals("someField3", thirdFilter.getKey());
        assertNotNull(thirdFilter.getOperator());
        assertEquals(QueryFilter.Operator.GT, thirdFilter.getOperator());
        assertNotNull(thirdFilter.getValue());
        assertEquals("someValue3", thirdFilter.getValue());
        assertNotNull(thirdFilter.getChainOperations());
        assertTrue(thirdFilter.getChainOperations().isEmpty());
    }

    @Test
    public void testAndNestedOrFilter() {
        String andFilter = "(someField1 eq someValue1) AND ((someField2 ne someValue2) OR (someField3 gt someValue3))";
        QueryFilter queryFilter = QueryFilterParser.parse(andFilter);
        assertNotNull(queryFilter);
        assertNull(queryFilter.getKey());
        assertNull(queryFilter.getValue());
        assertNotNull(queryFilter.getOperator());
        assertEquals(QueryFilter.Operator.AND, queryFilter.getOperator());
        assertNotNull(queryFilter.getChainOperations());
        assertEquals(2, queryFilter.getChainOperations().size());

        QueryFilter firstFilter = queryFilter.getChainOperations().get(0);
        assertNotNull(firstFilter.getKey());
        assertEquals("someField1", firstFilter.getKey());
        assertNotNull(firstFilter.getOperator());
        assertEquals(QueryFilter.Operator.EQ, firstFilter.getOperator());
        assertNotNull(firstFilter.getValue());
        assertEquals("someValue1", firstFilter.getValue());
        assertNotNull(firstFilter.getChainOperations());
        assertTrue(firstFilter.getChainOperations().isEmpty());

        QueryFilter secondFilter = queryFilter.getChainOperations().get(1);
        assertNull(secondFilter.getKey());
        assertNull(secondFilter.getValue());
        assertNotNull(secondFilter.getOperator());
        assertEquals(QueryFilter.Operator.OR, secondFilter.getOperator());
        assertNotNull(secondFilter.getChainOperations());
        assertEquals(2, secondFilter.getChainOperations().size());

        QueryFilter thirdFilter = secondFilter.getChainOperations().get(0);
        assertNotNull(thirdFilter.getKey());
        assertEquals("someField2", thirdFilter.getKey());
        assertNotNull(thirdFilter.getOperator());
        assertEquals(QueryFilter.Operator.NE, thirdFilter.getOperator());
        assertNotNull(thirdFilter.getValue());
        assertEquals("someValue2", thirdFilter.getValue());
        assertNotNull(thirdFilter.getChainOperations());
        assertTrue(thirdFilter.getChainOperations().isEmpty());

        QueryFilter fourthFilter = secondFilter.getChainOperations().get(1);
        assertNotNull(fourthFilter.getKey());
        assertEquals("someField3", fourthFilter.getKey());
        assertNotNull(fourthFilter.getOperator());
        assertEquals(QueryFilter.Operator.GT, fourthFilter.getOperator());
        assertNotNull(fourthFilter.getValue());
        assertEquals("someValue3", fourthFilter.getValue());
        assertNotNull(fourthFilter.getChainOperations());
        assertTrue(fourthFilter.getChainOperations().isEmpty());
    }

    @Test
    public void testNestedAndOrFilter() {
        String nestedAndOrFilter = "((someField1 eq someValue1) AND (someField2 ne someValue2)) OR (someField3 gt someValue3)";
        QueryFilter queryFilter = QueryFilterParser.parse(nestedAndOrFilter);
        assertNotNull(queryFilter);
        assertNull(queryFilter.getKey());
        assertNull(queryFilter.getValue());
        assertNotNull(queryFilter.getOperator());
        assertEquals(QueryFilter.Operator.OR, queryFilter.getOperator());
        assertNotNull(queryFilter.getChainOperations());
        assertEquals(2, queryFilter.getChainOperations().size());

        QueryFilter secondFilter = queryFilter.getChainOperations().get(0);
        assertNull(secondFilter.getKey());
        assertNull(secondFilter.getValue());
        assertNotNull(secondFilter.getOperator());
        assertEquals(QueryFilter.Operator.AND, secondFilter.getOperator());
        assertNotNull(secondFilter.getChainOperations());
        assertEquals(2, secondFilter.getChainOperations().size());
    }

    @Test
    public void testNoParenthesis() {
        String andFilter = "someField1 eq someValue1 AND someField2 ne someValue2";
        QueryFilter queryFilter = QueryFilterParser.parse(andFilter);
        assertNotNull(queryFilter);
    }
}
