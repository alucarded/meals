package com.devpeer.calories.user;

import com.devpeer.calories.meal.model.RegistrationForm;
import com.devpeer.calories.core.query.QueryFilter;
import com.devpeer.calories.core.query.QueryFilterParser;
import com.devpeer.calories.user.model.Authority;
import com.devpeer.calories.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/v1/users")
public class UsersRestController {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersRestController(UserRepository userRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // TODO: add exception handling and mapping to HTTP failures

    @GetMapping("/me")
    public ResponseEntity getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(UserMapper.from(userDetails));
    }

    @PutMapping("/me")
    public ResponseEntity updateCurrentUser(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestBody User user) {
        // TODO: add field validations
        user.setAuthorities(Collections.singletonList(Authority.USER));
        Optional.ofNullable(user.getPassword())
                .ifPresent(password -> user.setPassword(passwordEncoder.encode(password)));
        return ResponseEntity.ok(userRepository.save(user));
    }

    @DeleteMapping("/me")
    public ResponseEntity deleteCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        userRepository.deleteById(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity getAllUsers(@RequestParam(value = "filter", required = false) String filter,
                                      @RequestParam(value = "page") Integer page,
                                      @RequestParam(value = "size") Integer size) {
        if (filter != null) {
            QueryFilter queryFilter = QueryFilterParser.parse(filter, User.class);
            return ResponseEntity.ok(userRepository.findAll(queryFilter, PageRequest.of(page, size)));
        }

        return ResponseEntity.ok(userRepository.findAll(PageRequest.of(page, size)));
    }

    @PostMapping
    public ResponseEntity registerUser(@Valid @RequestBody RegistrationForm registrationForm) {
        User user = User.builder()
                .username(registrationForm.getUsername())
                .password(passwordEncoder.encode(registrationForm.getPassword()))
                .authorities(Collections.singletonList(Authority.USER))
                .build();
        return ResponseEntity.ok(UserMapper.from(userRepository.save(user)));
    }

    /**
     * User account upsert for user managers and administrators.
     *
     * @param user
     * @return
     */
    @PutMapping
    public ResponseEntity upsertUser(@RequestBody User user) {
        // Always encode password
        Optional.ofNullable(user.getPassword())
                .ifPresent(password -> user.setPassword(passwordEncoder.encode(password)));

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    // TODO: what if username is "me" - collision ?
    @GetMapping("/{id}")
    public ResponseEntity getUserById(@PathVariable("id") String id) {
        return userRepository.findById(id)
                .map(UserMapper::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUserById(@PathVariable("id") String id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}