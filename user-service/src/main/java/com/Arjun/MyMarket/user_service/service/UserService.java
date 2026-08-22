package com.Arjun.MyMarket.user_service.service;



import com.Arjun.MyMarket.user_service.dto.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse getById(UUID id);

    UserResponse createUser(UserResponse userResponse);

    UserResponse getByEmail(String email);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(UUID id, UserResponse userResponse);

    void deleteUser(UUID id);
}
