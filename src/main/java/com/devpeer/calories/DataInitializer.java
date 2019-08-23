package com.devpeer.calories;

import com.devpeer.calories.user.model.Authority;
import com.devpeer.calories.user.model.User;
import com.devpeer.calories.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
@Profile("development")
public class DataInitializer implements CommandLineRunner {

    @Autowired
    UserRepository users;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        try {
            this.users.save(User.builder()
                    .username("user")
                    .password(this.passwordEncoder.encode("password"))
                    .authorities(Arrays.asList(Authority.USER))
                    .build()
            );
        } catch (DuplicateKeyException e) {
            log.info("Test user already in DB", e);
        }

        try {
            this.users.save(User.builder()
                    .username("admin")
                    .password(this.passwordEncoder.encode("password"))
                    .authorities(Arrays.asList(Authority.USER, Authority.ADMIN))
                    .build()
            );
        } catch (DuplicateKeyException e) {
            log.info("Test admin already in DB", e);
        }

        log.debug("Printing test users...");
        this.users.findAll().forEach(v -> log.debug(" User :" + v.toString()));
    }
}