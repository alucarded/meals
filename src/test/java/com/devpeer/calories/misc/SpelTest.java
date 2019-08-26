package com.devpeer.calories.misc;

import com.devpeer.calories.meal.model.Meal;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SpelTest {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Ignore
    @Test
    public void testSpelExpression() {
        ExpressionParser expressionParser = new SpelExpressionParser();
        // TODO: why 'eq' operator is not supported correctly for LocalDateTime ?
        // TODO: also, LocalDate is not Comparable...
        Expression expression = expressionParser.parseExpression(
                "(date gt '2019-08-21') AND ((calories gt 20) OR (text eq 'hello'))");
        System.out.println(expression.getExpressionString());
        Meal meal = Meal.builder()
                .calories(10)
                .date(LocalDate.parse("2019-08-22", DATE_FORMATTER))
                .text("hello")
                .build();
        System.out.println(meal.toString());
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext(meal);
        Boolean result = expression.getValue(evaluationContext, Boolean.class);
        assertNotNull(result);
        assertTrue(result);
    }
}
