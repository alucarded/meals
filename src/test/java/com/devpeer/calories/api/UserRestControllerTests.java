package com.devpeer.calories.api;

import com.devpeer.calories.CaloriesApplication;
import com.devpeer.calories.api.model.RegistrationForm;
import com.devpeer.calories.auth.CustomUserDetailsService;
import com.devpeer.calories.auth.jwt.JwtTokenProvider;
import com.devpeer.calories.auth.user.Authority;
import com.devpeer.calories.auth.user.User;
import com.devpeer.calories.auth.user.UserRepository;
import com.devpeer.calories.core.Jackson;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = {UserRestController.class})
@ContextConfiguration(classes = {CaloriesApplication.class, JwtTokenProvider.class, CustomUserDetailsService.class})
public class UserRestControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    private static final String TEST_USERNAME = "username";

    private static final User TEST_USER = User.builder()
            .id("123")
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
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username", is(TEST_USERNAME)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].password").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].authorities", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].authorities[0]", is(Authority.USER.toString())));
    }

    @Test
    @WithMockUser
    public void givenNormalUser_whenGetUsers_thenReturnAccessDenied() throws Exception {
        List<User> allUsers = Arrays.asList(TEST_USER);

        given(userRepository.findAll()).willReturn(allUsers);

        mvc.perform(get("/v1/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"USER", "MANAGER"})
    public void givenUser_whenGetUserById_thenReturnUserOrNotFound() throws Exception {

        given(userRepository.findById(TEST_USER.getId())).willReturn(Optional.of(TEST_USER));

        mvc.perform(get("/v1/users/123").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", is(TEST_USER.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorities[0]", is(Authority.USER.toString())));

        mvc.perform(get("/v1/users/456").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", is(registrationForm.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorities[0]", is(Authority.USER.toString())));
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", is(TEST_USER.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist());
    }

    @Test
    @WithMockUser(authorities = {"USER", "MANAGER"})
    public void givenUserId_whenDeleteUserById_thenReturnNoContent() throws Exception {
        mvc.perform(delete("/v1/users/123").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
