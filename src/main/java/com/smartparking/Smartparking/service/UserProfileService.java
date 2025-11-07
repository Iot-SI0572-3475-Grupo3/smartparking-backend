package com.smartparking.Smartparking.service;

import com.smartparking.Smartparking.dto.request.UserProfileRequestDto;
import com.smartparking.Smartparking.dto.response.UserProfileResponseDto;

public interface UserProfileService {
    UserProfileResponseDto createUserProfile(String userId, UserProfileRequestDto profileRequestDto);
    UserProfileResponseDto findByUserId(String userId);
    UserProfileResponseDto updateUserProfile(String userId, UserProfileRequestDto profileRequestDto);
    void deleteUserProfile(String userId);
}