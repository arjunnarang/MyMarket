package com.Arjun.MyMarket.user_service.repository;

import com.Arjun.MyMarket.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
