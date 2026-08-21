package com.Arjun.MyMarket.user_service.service.impl;



import com.Arjun.MyMarket.user_service.dto.UserResponse;
import com.Arjun.MyMarket.user_service.entity.User;
import com.Arjun.MyMarket.user_service.exception.ResourceNotFoundException;
import com.Arjun.MyMarket.user_service.repository.UserRepository;
import com.Arjun.MyMarket.user_service.service.UserService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse getById(UUID id){
        User user = userRepository.findById(id)
                .orElseThrow(() ->new ResourceNotFoundException("User not found with id: " + id));

        UserResponse userResponse = toResponseDto(user);
        return userResponse;
    }

    private UserResponse toResponseDto(User user){
        UserResponse response= new UserResponse();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPassword(user.getPassword());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAddress(user.getAddress());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;
    }
}
