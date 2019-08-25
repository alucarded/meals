package com.devpeer.calories.user;

import com.devpeer.calories.user.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<User, String>, CustomUserRepository {
    Optional<User> findByUsername(String username);
}