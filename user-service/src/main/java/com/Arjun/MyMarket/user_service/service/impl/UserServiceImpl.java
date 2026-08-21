package com.Arjun.MyMarket.user_service.service.impl;



import com.Arjun.MyMarket.user_service.dto.UserResponse;
import com.Arjun.MyMarket.user_service.entity.Role;
import com.Arjun.MyMarket.user_service.entity.User;
import com.Arjun.MyMarket.user_service.exception.InvalidRequestException;
import com.Arjun.MyMarket.user_service.exception.ResourceNotFoundException;
import com.Arjun.MyMarket.user_service.repository.UserRepository;
import com.Arjun.MyMarket.user_service.service.UserService;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(UUID id){
        User user = userRepository.findById(id)
                .orElseThrow(() ->new ResourceNotFoundException("User not found with id: " + id));

        UserResponse userResponse = toResponseDto(user);
        return userResponse;
    }

    @Override
    public UserResponse getByEmail(String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return toResponseDto(user);
    }


    @Override
    public UserResponse createUser(UserResponse userResponse){
        if(userRepository.existsByEmail(userResponse.getEmail())){
            throw new InvalidRequestException("Email already exists");
        }

        User user = new User();
        user.setName(userResponse.getName());
        user.setEmail(userResponse.getEmail());
        user.setPassword(userResponse.getPassword());
        user.setPhoneNumber(userResponse.getPhoneNumber());
        user.setAddress(userResponse.getAddress());
        user.setRole(Role.GUEST);

       User savedUser = userRepository.save(user);

       return toResponseDto(savedUser);
    }

    private UserResponse toResponseDto(User user){

        log.debug("This is user entity: {}", user);

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
        System.out.println(response.getCreatedAt());
        return response;
    }
}
