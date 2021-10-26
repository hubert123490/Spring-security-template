package com.learningplatform.webapp.security.logic.service;


import com.learningplatform.webapp.security.model.dto.UserDto;
import com.learningplatform.webapp.security.model.response.OperationStatusModel;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto user);
    void deleteUser(String userId);
    List<UserDto> getUsers(int page, int limit);
    UserDto getUser(String email);
    UserDto getUserByUserId(String userId);
    UserDto updateUser(String userId, UserDto user);
    boolean verifyEmailToken(String token);
    boolean requestPasswordReset(String email);
    boolean resetPassword(String token, String password);
}
