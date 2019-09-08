package com.devpeer.calories.user.model;

import com.devpeer.calories.core.RegularExpressions;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User implements UserDetails {
    //@Id
    @Indexed(unique = true)
    @Pattern(regexp = RegularExpressions.USERNAME_REGEXP,
            message = RegularExpressions.USERNAME_REGEXP_DESCRIPTION)
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Pattern(regexp = RegularExpressions.PASSWORD_REGEXP,
            message = RegularExpressions.PASSWORD_REGEXP_DESCRIPTION)
    private String password;
    @Builder.Default
    private List<Authority> authorities = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}