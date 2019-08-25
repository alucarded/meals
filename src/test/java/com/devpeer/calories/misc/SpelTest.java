package com.devpeer.calories.misc;

import com.devpeer.calories.core.CustomTypeComparator;
import com.devpeer.calories.meal.model.Meal;
import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SpelTest {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    public void testSpelExpression() {
        ExpressionParser expressionParser = new SpelExpressionParser();
        // TODO: why 'eq' operator is not supported correctly for LocalDateTime ?
        Expression expression = expressionParser.parseExpression(
                "(dateTime gt '2019-08-21 00:00:00') AND ((calories gt 20) OR (text eq 'hello'))");
        System.out.println(expression.getExpressionString());
        Meal meal = Meal.builder()
                .calories(10)
                .dateTime(LocalDateTime.parse("2019-08-22 00:00:00", DATE_TIME_FORMATTER))
                .text("hello")
                .build();
        System.out.println(meal.toString());
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext(meal);
        //evaluationContext.setVariable("dateTime", meal.getDateTime().toEpochSecond(ZoneOffset.UTC));
        evaluationContext.setTypeComparator(
                new CustomTypeComparator(DATE_TIME_FORMATTER));
        Boolean result = expression.getValue(evaluationContext, Boolean.class);
        assertNotNull(result);
        assertTrue(result);
    }
}
