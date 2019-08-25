package com.devpeer.calories.misc;

import com.devpeer.calories.meal.model.Meal;
import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SpelTest {

    @Test
    public void testSpelExpression() {
        ExpressionParser expressionParser = new SpelExpressionParser();
        Expression expression = expressionParser.parseExpression("((calories gt 20) OR (text eq 'hello'))");
        System.out.println(expression.getExpressionString());
        Meal meal = Meal.builder()
                .calories(40)
                .text("hello")
                .build();
        EvaluationContext evaluationContext = new StandardEvaluationContext(meal);
        Boolean result = expression.getValue(evaluationContext, Boolean.class);
        assertNotNull(result);
        assertTrue(result);
    }
}
