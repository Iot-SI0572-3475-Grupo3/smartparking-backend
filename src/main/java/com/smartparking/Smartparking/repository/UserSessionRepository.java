package com.smartparking.Smartparking.repository;


import com.smartparking.Smartparking.entity.iam.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, String> {
    List<UserSession> findByUserIdAndIsActive(String userId, Boolean isActive);

    Optional<UserSession> findByTokenHashAndIsActive(String tokenHash, Boolean isActive);

    Optional<UserSession> findByUserIdAndTokenHashAndIsActiveTrue(String userId, String tokenHash);

    @Query("SELECT s FROM UserSession s WHERE s.userId = :userId AND s.expiresAt < CURRENT_TIMESTAMP")
    List<UserSession> findExpiredSessionsByUserId(String userId);
}