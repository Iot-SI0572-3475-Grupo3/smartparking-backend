package com.smartparking.Smartparking.controller;

import com.smartparking.Smartparking.dto.request.UserSessionRequestDto;
import com.smartparking.Smartparking.dto.response.UserSessionResponseDto;
import com.smartparking.Smartparking.entity.iam.UserSession;
import com.smartparking.Smartparking.service.UserSessionService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user-sessions")
public class UserSessionController {

    @Autowired
    private UserSessionService userSessionService;

    @PostMapping("/{userId}")
    public ResponseEntity<UserSessionResponseDto> createUserSession(@PathVariable String userId, @Valid @RequestBody UserSessionRequestDto sessionRequestDto) {
        UserSession session = userSessionService.createUserSession(userId, sessionRequestDto.getTokenHash());
        return ResponseEntity.ok(convertToResponseDto(session));
    }

    @GetMapping("/active/{userId}")
    public ResponseEntity<List<UserSessionResponseDto>> getActiveSessions(@PathVariable String userId) {
        List<UserSession> sessions = userSessionService.findActiveSessionsByUserId(userId);
        List<UserSessionResponseDto> response = sessions.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/token/{tokenHash}")
    public ResponseEntity<UserSessionResponseDto> getSessionByToken(@PathVariable String tokenHash) {
        UserSession session = userSessionService.findByTokenHash(tokenHash);
        return ResponseEntity.ok(convertToResponseDto(session));
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> invalidateSession(@PathVariable String sessionId) {
        userSessionService.invalidateSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    private UserSessionResponseDto convertToResponseDto(UserSession session) {
        UserSessionResponseDto dto = new UserSessionResponseDto();
        dto.setSessionId(session.getSessionId());
        dto.setUserId(session.getUserId());
        dto.setTokenHash(session.getTokenHash());
        dto.setCreatedAt(session.getCreatedAt());
        dto.setExpiresAt(session.getExpiresAt());
        dto.setIsActive(session.getIsActive());
        return dto;
    }
}