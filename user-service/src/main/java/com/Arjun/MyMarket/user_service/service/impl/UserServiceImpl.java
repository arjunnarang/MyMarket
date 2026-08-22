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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    @Transactional(readOnly = true)
    public UserResponse getByEmail(String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return toResponseDto(user);
    }


    @Override
    @Transactional(readOnly = true)
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

    @Override
    public List<UserResponse> getAllUsers(){
        List<User> users = userRepository.findAll();

        List<UserResponse> userResponses = users.stream().map(item -> toResponseDto(item)).collect(Collectors.toList());

        return userResponses;
    }

    @Override
    public UserResponse updateUser(UUID id, UserResponse userResponse){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        if(userResponse.getEmail() != null && !userResponse.getEmail().equals(user.getEmail())){
            if(userRepository.existsByEmail(userResponse.getEmail())){
                throw new InvalidRequestException(("Email already in use: " + userResponse.getEmail()));
            }
            userResponse.setEmail(userResponse.getEmail());
        }

        if(userResponse.getName() != null){
            user.setName(userResponse.getName());
        }

        if (userResponse.getPassword() != null) {
            user.setPassword(userResponse.getPassword());
        }
        if (userResponse.getPhoneNumber() != null) {
            user.setPhoneNumber(userResponse.getPhoneNumber());
        }
        if (userResponse.getAddress() != null) {
            user.setAddress(userResponse.getAddress());
        }
        if (userResponse.getRole() != null) {
            user.setRole(userResponse.getRole());
        }

        User savedUser = userRepository.save(user);

        return toResponseDto(savedUser);
    }

    @Override
    public void deleteUser(UUID id){
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
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
