package com.devpeer.calories.user;

import com.devpeer.calories.core.query.QueryFilter;
import com.devpeer.calories.core.query.QueryFilterParser;
import com.devpeer.calories.meal.model.RegistrationForm;
import com.devpeer.calories.user.model.Authority;
import com.devpeer.calories.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @GetMapping("/me")
    public ResponseEntity getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(userDetails);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    @PutMapping("/me")
    public ResponseEntity updateCurrentUser(@AuthenticationPrincipal UserDetails userDetails,
                                            @Valid @RequestBody User user) {
        try {
            // Do not allow to change authorities
            if (!user.getAuthorities().isEmpty()) {
                // TODO: have a POJO for error and return JSON - easier to process by frontend
                return ResponseEntity.badRequest()
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("Not allowed to modify authorities");
            }
            List<Authority> authorities = userDetails.getAuthorities().stream()
                    .map(ga -> Authority.valueOf(ga.getAuthority()))
                    .collect(Collectors.toList());
            user.setAuthorities(authorities);
            Optional.ofNullable(user.getPassword())
                    .ifPresent(password -> user.setPassword(passwordEncoder.encode(password)));
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(userRepository.save(user));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity deleteCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            userRepository.deleteById(userDetails.getUsername());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    @GetMapping
    public ResponseEntity getAllUsers(@RequestParam(value = "filter", required = false) String filter,
                                      @RequestParam(value = "page") Integer page,
                                      @RequestParam(value = "size") Integer size) {
        try {
            if (filter != null) {
                QueryFilter queryFilter = QueryFilterParser.parse(filter, User.class);
                return ResponseEntity.ok(userRepository.findAll(queryFilter, PageRequest.of(page, size)));
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(userRepository.findAll(PageRequest.of(page, size)));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    @PostMapping
    public ResponseEntity registerUser(@Valid @RequestBody RegistrationForm registrationForm) {
        try {
            User user = User.builder()
                    .username(registrationForm.getUsername())
                    .password(passwordEncoder.encode(registrationForm.getPassword()))
                    .authorities(Collections.singletonList(Authority.USER))
                    .build();
            return ResponseEntity.created(URI.create("/v1/users/" + registrationForm.getUsername()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(userRepository.save(user));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    /**
     * User account upsert for user managers and administrators.
     *
     * @param user
     * @return
     */
    @PutMapping
    public ResponseEntity upsertUser(@Valid @RequestBody User user) {
        try {
            // Always encode password
            Optional.ofNullable(user.getPassword())
                    .ifPresent(password -> user.setPassword(passwordEncoder.encode(password)));

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(userRepository.save(user));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    // TODO: what if username is "me" - collision ?
    @GetMapping("/{id}")
    public ResponseEntity getUserById(@PathVariable("id") String id) {
        try {
            return userRepository.findById(id)
                    .map(UserMapper::from)
                    .map(userMap -> ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(userMap))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUserById(@PathVariable("id") String id) {
        try {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }
}