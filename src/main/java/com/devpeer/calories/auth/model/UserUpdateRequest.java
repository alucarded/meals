package com.devpeer.calories.auth.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
public class UserUpdateRequest {
    @Nullable
    private String username;
    @Nullable
    private String password;
}
