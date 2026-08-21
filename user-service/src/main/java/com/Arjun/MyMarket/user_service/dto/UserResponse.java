package com.Arjun.MyMarket.user_service.dto;


import com.Arjun.MyMarket.user_service.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private UUID id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "password is required")
    private String password;

    private String phoneNumber;

    private String address;
    private Role role;
    private Instant createdAt;
    private Instant updatedAt;
}
