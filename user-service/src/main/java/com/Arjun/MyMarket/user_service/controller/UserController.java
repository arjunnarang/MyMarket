package com.Arjun.MyMarket.user_service.controller;


import com.Arjun.MyMarket.user_service.dto.UserResponse;
import com.Arjun.MyMarket.user_service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id){
        return ResponseEntity.ok().body(userService.getById(id));
    }


    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserResponse userResponse){
        return new ResponseEntity<>(userService.createUser(userResponse), HttpStatus.CREATED);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getByEmail(@PathVariable String email){
        return ResponseEntity.ok().body(userService.getByEmail(email));
    }
}
