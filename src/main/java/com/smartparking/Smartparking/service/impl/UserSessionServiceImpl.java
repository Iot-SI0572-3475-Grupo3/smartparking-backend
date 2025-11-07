package com.smartparking.Smartparking.service.impl;

import com.smartparking.Smartparking.entity.iam.UserSession;
import com.smartparking.Smartparking.repository.UserSessionRepository;
import com.smartparking.Smartparking.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserSessionServiceImpl implements UserSessionService {

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Override
    @Transactional
    public UserSession createUserSession(String userId, String tokenHash) {
        UserSession session = new UserSession();
        session.setSessionId(java.util.UUID.randomUUID().toString());
        session.setUserId(userId);
        session.setTokenHash(tokenHash);
        session.setCreatedAt(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusHours(24)); // Ejemplo: 24 horas de expiración
        session.setIsActive(true);

        return userSessionRepository.save(session);
    }

    @Override
    @Transactional(readOnly = true)
    public UserSession findByTokenHash(String tokenHash) {
        return userSessionRepository.findByTokenHashAndIsActive(tokenHash, true)
                .orElseThrow(() -> new RuntimeException("Active session not found for token: " + tokenHash));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSession> findActiveSessionsByUserId(String userId) {
        return userSessionRepository.findByUserIdAndIsActive(userId, true);
    }

    @Override
    @Transactional
    public void invalidateSession(String sessionId) {
        UserSession session = userSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + sessionId));
        session.setIsActive(false);
        userSessionRepository.save(session);
    }
}