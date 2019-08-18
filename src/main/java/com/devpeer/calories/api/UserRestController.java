package com.devpeer.calories.api;

import com.devpeer.calories.auth.user.Role;
import com.devpeer.calories.auth.user.User;
import com.devpeer.calories.auth.user.UserRepository;
import com.devpeer.calories.auth.user.UserMapper;
import com.devpeer.calories.api.model.RegistrationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/v1/users")
public class UserRestController {

    private UserRepository userRepository;

    @Autowired
    public UserRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        // TODO
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/me")
    public ResponseEntity deleteCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        // TODO
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping
    public ResponseEntity getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping
    public ResponseEntity registerUser(@RequestBody RegistrationForm registrationForm) {
        User user = User.builder()
                .username(registrationForm.getUsername())
                .password(registrationForm.getPassword())
                .roles(Collections.singletonList(Role.ROLE_USER))
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