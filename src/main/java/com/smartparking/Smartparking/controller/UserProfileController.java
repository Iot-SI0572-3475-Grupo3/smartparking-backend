package com.smartparking.Smartparking.controller;

import com.smartparking.Smartparking.dto.request.UserProfileRequestDto;
import com.smartparking.Smartparking.dto.response.UserProfileResponseDto;
import com.smartparking.Smartparking.service.UserProfileService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user-profiles")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @PostMapping("/{userId}")
    public ResponseEntity<UserProfileResponseDto> createUserProfile(@PathVariable String userId, @Valid @RequestBody UserProfileRequestDto profileRequestDto) {
        UserProfileResponseDto response = userProfileService.createUserProfile(userId, profileRequestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponseDto> getUserProfile(@PathVariable String userId) {
        UserProfileResponseDto response = userProfileService.findByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileResponseDto> updateUserProfile(@PathVariable String userId, @Valid @RequestBody UserProfileRequestDto profileRequestDto) {
        UserProfileResponseDto response = userProfileService.updateUserProfile(userId, profileRequestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserProfile(@PathVariable String userId) {
        userProfileService.deleteUserProfile(userId);
        return ResponseEntity.noContent().build();
    }
}