package com.devpeer.calories.api;

import com.devpeer.calories.CaloriesApplication;
import com.devpeer.calories.auth.model.RegistrationForm;
import com.devpeer.calories.auth.CustomUserDetailsService;
import com.devpeer.calories.auth.jwt.JwtTokenProvider;
import com.devpeer.calories.user.model.Authority;
import com.devpeer.calories.user.model.User;
import com.devpeer.calories.user.UserRepository;
import com.devpeer.calories.core.jackson.Jackson;
import com.devpeer.calories.user.UsersRestController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = {UsersRestController.class})
@ContextConfiguration(classes = {CaloriesApplication.class, JwtTokenProvider.class, CustomUserDetailsService.class})
public class UsersRestControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    private static final String TEST_USERNAME = "username";

    private static final User TEST_USER = User.builder()
            .username(TEST_USERNAME)
            .password("password")
            .authorities(Collections.singletonList(Authority.USER))
            .build();

    @Test
    @WithMockUser(authorities = {"USER", "MANAGER"})
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        List<User> allUsers = Arrays.asList(TEST_USER);

        given(userRepository.findAll()).willReturn(allUsers);

        mvc.perform(get("/v1/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(TEST_USERNAME)))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[0].authorities", hasSize(1)))
                .andExpect(jsonPath("$[0].authorities[0]", is(Authority.USER.toString())));
    }

    @Test
    @WithMockUser
    public void givenUsers_whenGetUsers_thenReturnAccessDenied() throws Exception {
        List<User> allUsers = Arrays.asList(TEST_USER);

        given(userRepository.findAll()).willReturn(allUsers);

        mvc.perform(get("/v1/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @WithMockUser(authorities = {"USER", "MANAGER"})
    public void givenUser_whenGetUserByUsername_thenReturnUserOrNotFound() throws Exception {

        given(userRepository.findById(TEST_USER.getUsername())).willReturn(Optional.of(TEST_USER));

        mvc.perform(get("/v1/users/" + TEST_USER.getUsername()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(TEST_USER.getUsername())))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.authorities[0]", is(Authority.USER.toString())));

        mvc.perform(get("/v1/users/qwerty").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void givenUser_whenGetUserByUsername_thenReturnAccessDenied() throws Exception {

        given(userRepository.findById(TEST_USER.getUsername())).willReturn(Optional.of(TEST_USER));

        mvc.perform(get("/v1/users/123").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());
    }

    // All API users can register
    @Test
    public void givenRegistrationForm_whenRegisterUser_thenReturnUser() throws Exception {
        // TODO: password length and characters validation
        // TODO: prevent registered user from registering again ?
        RegistrationForm registrationForm = new RegistrationForm("newuser1", "password");

        given(userRepository.save(any())).willReturn(User.builder()
                .username(registrationForm.getUsername())
                .password(registrationForm.getPassword())
                .authorities(Collections.singletonList(Authority.USER))
                .build());

        mvc.perform(post("/v1/users").contentType(MediaType.APPLICATION_JSON).content(Jackson.toJsonUnsafe(registrationForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(registrationForm.getUsername())))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.authorities[0]", is(Authority.USER.toString())));
    }

    @Test
    @WithMockUser(authorities = {"USER", "MANAGER"})
    public void givenUser_whenUpsertUser_thenReturnUser() throws Exception {

        User returnedUser = User.builder()
                .username(TEST_USERNAME)
                .authorities(Collections.singletonList(Authority.USER))
                .build();

        given(userRepository.save(TEST_USER)).willReturn(returnedUser);

        mvc.perform(put("/v1/users").contentType(MediaType.APPLICATION_JSON).content(Jackson.toJsonUnsafe(TEST_USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(TEST_USER.getUsername())))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @WithMockUser
    public void givenUser_whenUpsertUser_thenReturnAccessDenied() throws Exception {

        User returnedUser = User.builder()
                .username(TEST_USERNAME)
                .authorities(Collections.singletonList(Authority.USER))
                .build();

        given(userRepository.save(TEST_USER)).willReturn(returnedUser);

        mvc.perform(put("/v1/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @WithMockUser(authorities = {"USER", "MANAGER"})
    public void givenUserId_whenDeleteUserById_thenReturnNoContent() throws Exception {
        mvc.perform(delete("/v1/users/123").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void givenUserId_whenDeleteUserById_thenReturnAccessDenied() throws Exception {
        mvc.perform(delete("/v1/users/123").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());
    }


    // ME

    @Test
    @WithMockUser(username = "username", authorities = {"USER"})
    public void whenGetCurrentUser_thenReturnUserInfo() throws Exception {
        mvc.perform(get("/v1/users/me").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("username")));
    }

    @Test
    public void whenGetCurrentUser_thenReturnAccessDenied() throws Exception {
        mvc.perform(get("/v1/users/me").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void givenUser_whenUpdateCurrentUser_thenReturnUpdatedUserInfo() throws Exception {

        User updatedUser = TEST_USER;
        // TODO: make sure user cant modify his ID, authorities
        // TODO: should we allow username edit?
        // TODO: integration tests for password change ?
        updatedUser.setUsername("newusername");

        given(userRepository.save(updatedUser)).willReturn(updatedUser);

        mvc.perform(put("/v1/users/me").contentType(MediaType.APPLICATION_JSON).content(Jackson.toJsonUnsafe(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(Jackson.toJsonUnsafe(updatedUser)));
    }

    @Test
    public void givenUser_whenUpdateCurrentUser_thenReturnAccessDenied() throws Exception {

        User updatedUser = TEST_USER;
        given(userRepository.save(updatedUser)).willReturn(updatedUser);

        mvc.perform(put("/v1/users/me").contentType(MediaType.APPLICATION_JSON).content(Jackson.toJsonUnsafe(updatedUser)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @WithMockUser(username = "user123", authorities = {"USER"})
    public void whenDeleteCurrentUser_thenReturnNoContent() throws Exception {
        mvc.perform(delete("/v1/users/me").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        then(userRepository).should().deleteById("user123");
    }

    @Test
    public void givenUser_whenDeleteCurrentUser_thenReturnAccessDenied() throws Exception {
        mvc.perform(delete("/v1/users/me").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());
    }
}
