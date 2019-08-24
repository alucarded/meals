package com.devpeer.calories.meal.repository;

import com.devpeer.calories.core.Jackson;
import com.devpeer.calories.core.query.MongoQueryBuilder;
import com.devpeer.calories.core.query.QueryFilter;
import com.devpeer.calories.meal.model.Meal;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
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
    public Page<Meal> findAll(QueryFilter queryFilter, Pageable pageable) {
        Query query = MongoQueryBuilder.build(queryFilter);
        List<Meal> list = mongoTemplate.find(query, Meal.class);
        return PageableExecutionUtils.getPage(
                list,
                pageable,
                () -> mongoTemplate.count(query, Meal.class));
    }
}
