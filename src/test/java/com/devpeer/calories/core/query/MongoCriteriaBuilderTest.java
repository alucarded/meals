package com.devpeer.calories.core.query;

import com.devpeer.calories.core.jackson.Jackson;
import com.devpeer.calories.core.query.MongoCriteriaBuilder;
import com.devpeer.calories.core.query.QueryFilter;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MongoCriteriaBuilderTest {

    @Data
    @NoArgsConstructor
    private static class SomeClass {
        private Integer someInteger;
        private Double someDouble;
    }

    @Test
    public void testBasicQuery() {
        QueryFilter queryFilter = new QueryFilter();
        queryFilter.setOperator(QueryFilter.Operator.EQ);
        queryFilter.setKey("someInteger");
        queryFilter.setValue(11);
        Criteria criteria = MongoCriteriaBuilder.create().build(queryFilter);
        Map<String, Object> queryMap = Jackson.fromJsonToMap(criteria.getCriteriaObject().toJson());
        System.out.println(criteria.getCriteriaObject().toJson());
        assertEquals(11, queryMap.get("someInteger"));
    }

    @Test
    public void testAndQuery() {
        QueryFilter eqFilter = new QueryFilter();
        eqFilter.setOperator(QueryFilter.Operator.EQ);
        eqFilter.setKey("someInteger");
        eqFilter.setValue(11);

        QueryFilter gtFilter = new QueryFilter();
        gtFilter.setOperator(QueryFilter.Operator.GT);
        gtFilter.setKey("someDouble");
        gtFilter.setValue(22.5D);

        QueryFilter andFilter = new QueryFilter();
        andFilter.setOperator(QueryFilter.Operator.AND);
        andFilter.setChainOperations(Arrays.asList(eqFilter, gtFilter));

        Criteria criteria = MongoCriteriaBuilder.create().build(andFilter);
        Map<String, Object> queryMap = Jackson.fromJsonToMap(criteria.getCriteriaObject().toJson());
        System.out.println(criteria.getCriteriaObject().toJson());
        // TODO: test
    }
}
