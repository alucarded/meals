package com.devpeer.calories.api;

import com.devpeer.calories.CaloriesApplication;
import com.devpeer.calories.auth.jwt.JwtTokenProvider;
import com.devpeer.calories.auth.user.Role;
import com.devpeer.calories.auth.user.User;
import com.devpeer.calories.auth.user.UserRepository;
import com.devpeer.calories.core.Jackson;
import com.devpeer.calories.api.model.AuthenticationRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
// TODO: provide security config instead of setting secure to false here
@WebMvcTest(value = AuthRestController.class, secure = false)
@ContextConfiguration(classes = {CaloriesApplication.class})
public class AuthRestControllerTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserRepository userRepository;

    // TODO: test roles

    @Test
    public void givenUserToken_whenSignin_thenReturnJwtOrUnauthorized() throws Exception {
        User user = User.builder()
                .username("user")
                .password("password")
                .roles(Collections.singletonList(Role.ROLE_USER))
                .build();
        given(userRepository.findByUsername("user")).willReturn(Optional.of(user));
        given(jwtTokenProvider.createToken("user", Collections.singletonList(Role.ROLE_USER))).willReturn("TOKEN");

        AuthenticationRequest authenticationRequest = new AuthenticationRequest("user", "password");

        mvc.perform(post("/v1/auth/signin").contentType(MediaType.APPLICATION_JSON).content(Jackson.toJsonUnsafe(authenticationRequest)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", is(user.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token", is("TOKEN")));

        AuthenticationRequest unauthorizedRequest = new AuthenticationRequest("user2", "invalid");

        mvc.perform(post("/v1/auth/signin").contentType(MediaType.APPLICATION_JSON).content(Jackson.toJsonUnsafe(unauthorizedRequest)))
                .andExpect(status().isUnauthorized());
    }
}
