package com.devpeer.calories.user;

import com.devpeer.calories.CaloriesApplication;
import com.devpeer.calories.auth.CustomUserDetailsService;
import com.devpeer.calories.auth.jwt.JwtTokenProvider;
import com.devpeer.calories.core.jackson.Jackson;
import com.devpeer.calories.core.query.QueryFilter;
import com.devpeer.calories.meal.model.RegistrationForm;
import com.devpeer.calories.user.model.Authority;
import com.devpeer.calories.user.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.support.PageableExecutionUtils;
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
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = {UsersRestController.class})
@ContextConfiguration(classes = {CaloriesApplication.class, JwtTokenProvider.class, CustomUserDetailsService.class})
@AutoConfigureRestDocs
public class UsersRestControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    @Captor
    ArgumentCaptor<User> userArgumentCaptor;

    private static final String TEST_USERNAME = "username";

    private static final User TEST_USER = User.builder()
            .username(TEST_USERNAME)
            .password("password")
            .authorities(Collections.singletonList(Authority.USER))
            .build();

    @Test
    @WithMockUser(authorities = {"USER", "MANAGER"})
    public void givenUsers_whenGetUsers_thenReturnPage() throws Exception {
        List<User> allUsers = Arrays.asList(TEST_USER);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<User> paged = PageableExecutionUtils.getPage(allUsers, pageRequest, () -> 1L);

        given(userRepository.findAll(PageRequest.of(0, 10))).willReturn(paged);

        mvc.perform(get("/v1/users?page=0&size=10").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].username", is(TEST_USERNAME)))
                .andExpect(jsonPath("$.content[0].password").doesNotExist())
                .andExpect(jsonPath("$.content[0].authorities", hasSize(1)))
                .andExpect(jsonPath("$.content[0].authorities[0]", is(Authority.USER.toString())))
                .andDo(document("users-get-with-pagination"));
    }

    @Test
    @WithMockUser(authorities = {"USER", "MANAGER"})
    public void givenUsers_whenGetUsersWithFilter_thenReturnPage() throws Exception {
        List<User> allUsers = Arrays.asList(TEST_USER);
        QueryFilter queryFilter = new QueryFilter();
        queryFilter.setKey("username");
        queryFilter.setValue("user");
        queryFilter.setOperator(QueryFilter.Operator.EQ);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<User> paged = PageableExecutionUtils.getPage(allUsers, pageRequest, () -> 1L);

        given(userRepository.findAll(queryFilter, PageRequest.of(0, 10))).willReturn(paged);

        mvc.perform(get("/v1/users?filter=username eq user&page=0&size=10").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].username", is(TEST_USERNAME)))
                .andExpect(jsonPath("$.content[0].password").doesNotExist())
                .andExpect(jsonPath("$.content[0].authorities", hasSize(1)))
                .andExpect(jsonPath("$.content[0].authorities[0]", is(Authority.USER.toString())))
                .andDo(document("users-get-with-filter"));
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
                .andExpect(jsonPath("$.authorities[0]", is(Authority.USER.toString())))
                .andDo(document("users-get-by-id"));

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
        // TODO: prevent registered user from registering again ?
        RegistrationForm registrationForm = new RegistrationForm("newuser1", "Password!1");

        given(userRepository.save(any())).willReturn(User.builder()
                .username(registrationForm.getUsername())
                .password(registrationForm.getPassword())
                .authorities(Collections.singletonList(Authority.USER))
                .build());

        mvc.perform(post("/v1/users").contentType(MediaType.APPLICATION_JSON).content(Jackson.toJsonUnsafe(registrationForm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is(registrationForm.getUsername())))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.authorities[0]", is(Authority.USER.toString())))
                .andDo(document("users-registration"));

        verify(userRepository).save(userArgumentCaptor.capture());
        assertNotEquals(registrationForm.getPassword(), userArgumentCaptor.getValue().getPassword());
    }

    // TODO: test regexp input validation works

    @Test
    @WithMockUser(authorities = {"USER", "MANAGER"})
    public void givenUser_whenUpsertUser_thenReturnUser() throws Exception {

        User returnedUser = User.builder()
                .username(TEST_USERNAME)
                .password("password")
                .authorities(Collections.singletonList(Authority.USER))
                .build();

        given(userRepository.save(any())).willReturn(returnedUser);

        mvc.perform(put("/v1/users").contentType(MediaType.APPLICATION_JSON).content(Jackson.toJsonUnsafe(TEST_USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(TEST_USER.getUsername())))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andDo(document("users-upsert"));

        verify(userRepository).save(userArgumentCaptor.capture());
        assertNotEquals(returnedUser.getPassword(), userArgumentCaptor.getValue().getPassword());
    }

    @Test
    @WithMockUser
    public void givenUser_whenUpsertUser_thenReturnAccessDenied() throws Exception {

        User returnedUser = User.builder()
                .username(TEST_USERNAME)
                .authorities(Collections.singletonList(Authority.USER))
                .build();

        given(userRepository.save(any())).willReturn(returnedUser);

        mvc.perform(put("/v1/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @WithMockUser(authorities = {"USER", "MANAGER"})
    public void givenUserId_whenDeleteUserById_thenReturnNoContent() throws Exception {
        mvc.perform(delete("/v1/users/123").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(document("users-delete-by-id"));
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
                .andExpect(jsonPath("$.username", is("username")))
                .andDo(document("users-get-current"));
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
        updatedUser.setAuthorities(null);

        given(userRepository.save(any())).willReturn(updatedUser);

        mvc.perform(put("/v1/users/me").contentType(MediaType.APPLICATION_JSON).content(Jackson.toJsonUnsafe(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(Jackson.toJsonUnsafe(updatedUser)))
                .andDo(document("users-put-current"));

        verify(userRepository).save(userArgumentCaptor.capture());
        assertNotEquals(updatedUser.getPassword(), userArgumentCaptor.getValue().getPassword());
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
                .andExpect(status().isNoContent())
                .andDo(document("users-delete-current"));
        then(userRepository).should().deleteById("user123");
    }

    @Test
    public void givenUser_whenDeleteCurrentUser_thenReturnAccessDenied() throws Exception {
        mvc.perform(delete("/v1/users/me").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());
    }
}
