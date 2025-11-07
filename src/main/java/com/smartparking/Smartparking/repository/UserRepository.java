package com.smartparking.Smartparking.repository;

import com.smartparking.Smartparking.entity.iam.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
}