package com.devpeer.calories;

import com.devpeer.calories.auth.user.Authority;
import com.devpeer.calories.auth.user.User;
import com.devpeer.calories.auth.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
@Profile("development")
public class DataInitializer implements CommandLineRunner {

    //...

    @Autowired
    UserRepository users;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // TODO: this is only for development purposes
        try {
            this.users.save(User.builder()
                    .username("user")
                    .password(this.passwordEncoder.encode("password"))
                    .authorities(Arrays.asList(Authority.USER))
                    .build()
            );

            this.users.save(User.builder()
                    .username("admin")
                    .password(this.passwordEncoder.encode("password"))
                    .authorities(Arrays.asList(Authority.USER, Authority.ADMIN))
                    .build()
            );
        } catch (Exception e) {
            log.error("Got exception when adding default users", e);
        }

        log.debug("printing all users...");
        this.users.findAll().forEach(v -> log.debug(" User :" + v.toString()));
    }
}