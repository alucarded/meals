package com.devpeer.calories.user;

import com.devpeer.calories.core.query.MongoCriteriaBuilder;
import com.devpeer.calories.core.query.QueryFilter;
import com.devpeer.calories.meal.model.Meal;
import com.devpeer.calories.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.util.List;

public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public CustomUserRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<User> findAll(QueryFilter queryFilter, Pageable pageable) {
        Criteria criteria = MongoCriteriaBuilder.create().build(queryFilter);
        Query query = new Query(criteria).with(pageable);
        List<User> list = mongoTemplate.find(query, User.class);
        return PageableExecutionUtils.getPage(
                list,
                pageable,
                list::size);
    }
}
