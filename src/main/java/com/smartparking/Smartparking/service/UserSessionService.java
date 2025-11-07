package com.smartparking.Smartparking.service;

import com.smartparking.Smartparking.entity.iam.UserSession;

import java.util.List;

public interface UserSessionService {
    UserSession createUserSession(String userId, String tokenHash);
    UserSession findByTokenHash(String tokenHash);
    List<UserSession> findActiveSessionsByUserId(String userId);
    void invalidateSession(String sessionId);
}