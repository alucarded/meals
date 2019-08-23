package com.devpeer.calories.user;

import com.devpeer.calories.auth.model.RegistrationForm;
import com.devpeer.calories.user.model.Authority;
import com.devpeer.calories.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/v1/users")
public class UsersRestController {

    private UserRepository userRepository;

    @Autowired
    public UsersRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(UserMapper.from(userDetails));
    }

    @PutMapping("/me")
    public ResponseEntity updateCurrentUser(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestBody User user) {
        // TODO: add field validations
        user.setAuthorities(Collections.singletonList(Authority.USER));
        return ResponseEntity.ok(userRepository.save(user));
    }

    @DeleteMapping("/me")
    public ResponseEntity deleteCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        userRepository.deleteById(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity getAllUsers() {
        Iterable<User> userIterable = userRepository.findAll();
        List<User> users = new ArrayList<>();
        userIterable.forEach(user -> {
            user.setPassword(null);
            users.add(user);
        });
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity registerUser(@RequestBody RegistrationForm registrationForm) {
        User user = User.builder()
                .username(registrationForm.getUsername())
                .password(registrationForm.getPassword())
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
        // TODO: make sure password is hashed
        User savedUser = userRepository.save(user);
        savedUser.setPassword(null);
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