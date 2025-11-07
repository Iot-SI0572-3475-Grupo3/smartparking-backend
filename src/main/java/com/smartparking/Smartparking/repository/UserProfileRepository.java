package com.smartparking.Smartparking.repository;

import com.smartparking.Smartparking.entity.iam.UserProfile;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
    Optional<UserProfile> findByUser_UserId(String userId);

    @Modifying
    @Transactional
    void deleteByUser_UserId(String userId);
}
