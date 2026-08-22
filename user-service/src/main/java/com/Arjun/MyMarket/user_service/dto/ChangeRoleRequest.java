package com.Arjun.MyMarket.user_service.dto;

import com.Arjun.MyMarket.user_service.entity.Role;

import java.util.UUID;

public record ChangeRoleRequest(
        UUID id,
        Role role
) {
}
