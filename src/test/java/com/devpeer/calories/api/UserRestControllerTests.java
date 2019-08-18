package com.devpeer.calories.api;

import com.devpeer.calories.CaloriesApplication;
import com.devpeer.calories.auth.user.Role;
import com.devpeer.calories.auth.user.User;
import com.devpeer.calories.auth.user.UserRepository;
import com.devpeer.calories.core.Jackson;
import com.devpeer.calories.model.input.RegistrationForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
// TODO: provide security config instead of setting secure to false here
@WebMvcTest(value = UserRestController.class, secure = false)
@ContextConfiguration(classes = {CaloriesApplication.class})
public class UserRestControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        User tom = new User();
        tom.setUsername("tommy123");

        List<User> allUsers = Arrays.asList(tom);

        given(userRepository.findAll()).willReturn(allUsers);

        mvc.perform(get("/v1/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username", is(tom.getUsername())));
    }

    @Test
    public void givenUser_whenGetUserById_thenReturnUserOrNotFound() throws Exception {
        User user = User.builder()
                .id("123")
                .username("tom")
                .roles(Arrays.asList(Role.ROLE_USER))
                .build();

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        mvc.perform(get("/v1/users/123").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", is(user.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0]", is(Role.ROLE_USER.toString())));

        mvc.perform(get("/v1/users/456").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenRegistrationForm_whenRegisterUser_thenReturnUser() throws Exception {
        // TODO: password length and characters validation
        RegistrationForm registrationForm = new RegistrationForm("newuser1", "password");

        given(userRepository.save(any())).willReturn(User.builder()
                .username(registrationForm.getUsername())
                .password(registrationForm.getPassword())
                .roles(Collections.singletonList(Role.ROLE_USER))
                .build());

        mvc.perform(post("/v1/users").contentType(MediaType.APPLICATION_JSON).content(Jackson.toJsonUnsafe(registrationForm)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", is(registrationForm.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0]", is(Role.ROLE_USER.toString())));
    }

    @Test
    public void givenUser_whenUpsertUser_thenReturnUser() throws Exception {
        User user = User.builder()
                .username("user1")
                .password("passwd1")
                .build();

        User returnedUser = User.builder()
                .username("user1")
                .build();

        given(userRepository.save(user)).willReturn(returnedUser);

        mvc.perform(put("/v1/users").contentType(MediaType.APPLICATION_JSON).content(Jackson.toJsonUnsafe(user)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", is(user.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist());
    }

    @Test
    public void givenUserId_whenDeleteUserById_thenReturnNoContent() throws Exception {
        mvc.perform(delete("/v1/users/123").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
