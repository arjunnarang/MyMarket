package com.Arjun.MyMarket.user_service.service;



import com.Arjun.MyMarket.user_service.dto.UserResponse;
import com.Arjun.MyMarket.user_service.entity.Role;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse getById(UUID id);

    UserResponse createUser(UserResponse userResponse);

    UserResponse getByEmail(String email);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(UUID id, UserResponse userResponse);

    void deleteUser(UUID id);

    void changeRole(UUID id, Role role);
}
