package com.devpeer.calories.meal.repository;

import com.devpeer.calories.core.jackson.Jackson;
import com.devpeer.calories.core.query.MongoCriteriaBuilder;
import com.devpeer.calories.core.query.QueryFilter;
import com.devpeer.calories.meal.model.CaloriesForDay;
import com.devpeer.calories.meal.model.Meal;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.util.List;

public class CustomMealRepositoryImpl implements CustomMealRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public CustomMealRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    // TODO: test this class separately

    @Override
    public Meal update(Meal meal) {
        return mongoTemplate.findAndModify(
                Query.query(Criteria.where("id").is(meal.getId()).and("userId").is(meal.getUserId())),
                Update.fromDocument(new Document(Jackson.toObjectMap(meal)), Meal.ID_FIELD_NAME, Meal.USER_ID_FIELD_NAME),
                new FindAndModifyOptions().returnNew(true),
                Meal.class);
    }

    @Override
    public List<Meal> findAllWithTotalCalories() {
        GroupOperation grouping = Aggregation.group("userId", "date")
                .sum("calories").as("totalCalories");
        OutOperation outOperation = Aggregation.out(CaloriesForDay.class.getSimpleName());
        mongoTemplate.aggregate(Aggregation.newAggregation(grouping, outOperation),
                Meal.class, CaloriesForDay.class).getMappedResults()
                .forEach(caloriesForDay -> System.out.println(caloriesForDay.toString()));

        LookupOperation lookup = Aggregation.lookup("CaloriesForDay", "userId", "_id.userId", "caloriesForDay");
        UnwindOperation unwindOperation = Aggregation.unwind("caloriesForDay");
        ProjectionOperation projectionOperation = Aggregation.project("date")
                .and(AggregationSpELExpression.expressionOf("cond(date == caloriesForDay._id.date, 1, 0)"))
                .as("matched")
                .andInclude("userId", "time", "text", "calories")
                .and("caloriesForDay.totalCalories").as("totalCalories");
        MatchOperation matchOperation = Aggregation.match(Criteria.where("matched").is(1));
        return mongoTemplate.aggregate(
                Aggregation.newAggregation(lookup, unwindOperation, projectionOperation, matchOperation),
                Meal.class, Meal.class)
                .getMappedResults();
    }

    @Override
    public Page<Meal> findAll(QueryFilter queryFilter, Pageable pageable) {
        Criteria criteria = MongoCriteriaBuilder.create().build(queryFilter);
        Query query = new Query(criteria).with(pageable);
        // TODO: send aggregate and find at once?
//        GroupOperation grouping = Aggregation.group("userId", "date")
//                .sum("calories").as("totalCalories");
//        LookupOperation lookup = Aggregation.lookup("Meal", "_id.userId", "userId", "Meals");
//        MatchOperation match = Aggregation.match(criteria);
//        Aggregation aggregation = Aggregation.newAggregation(
//                Aggregation.group("userId", "date")
//                        .sum("calories").as("totalCalories"));
//        aggregation.withOptions(new AggregationOptions())
//        AggregationResults<CaloriesForDay> aggregationResults =
//                mongoTemplate.aggregate(
//                        aggregation,
//                        Meal.class,
//                        CaloriesForDay.class);
        List<Meal> list = mongoTemplate.find(query, Meal.class);
        return PageableExecutionUtils.getPage(
                list,
                pageable,
                () -> mongoTemplate.count(query, Meal.class));
    }
}
