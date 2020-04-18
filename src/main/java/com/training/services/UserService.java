package com.training.services;

import com.training.models.internal.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    User getUserByUsername(String username);

    void registerCurrentUser(@NotNull User user);
}
