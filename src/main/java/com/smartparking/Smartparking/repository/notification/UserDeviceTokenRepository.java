package com.smartparking.Smartparking.repository.notification;

import com.smartparking.Smartparking.entity.notification.UserDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, String> {

    List<UserDeviceToken> findByUser_UserIdAndIsActiveTrue(String userId);

    Optional<UserDeviceToken> findByUser_UserIdAndToken(String userId, String token);
}