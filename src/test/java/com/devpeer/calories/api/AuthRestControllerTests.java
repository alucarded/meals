package com.devpeer.calories.api;

import com.devpeer.calories.CaloriesApplication;
import com.devpeer.calories.auth.CustomUserDetailsService;
import com.devpeer.calories.auth.jwt.JwtTokenProvider;
import com.devpeer.calories.auth.user.Authority;
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
@WebMvcTest(value = AuthRestController.class)
@ContextConfiguration(classes = {CaloriesApplication.class, JwtTokenProvider.class, CustomUserDetailsService.class})
public class AuthRestControllerTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void givenUserToken_whenSignin_thenReturnJwtOrUnauthorized() throws Exception {
        User user = User.builder()
                .username("user")
                .password("password")
                .authorities(Collections.singletonList(Authority.USER))
                .build();
        given(userRepository.findByUsername("user")).willReturn(Optional.of(user));
        given(jwtTokenProvider.createToken("user", Collections.singletonList(Authority.USER))).willReturn("TOKEN");

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
