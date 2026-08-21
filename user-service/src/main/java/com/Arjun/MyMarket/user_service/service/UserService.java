package com.Arjun.MyMarket.user_service.service;



import com.Arjun.MyMarket.user_service.dto.UserResponse;

import java.util.UUID;

public interface UserService {

    UserResponse getById(UUID id);
}
