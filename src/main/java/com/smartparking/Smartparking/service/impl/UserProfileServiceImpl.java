package com.smartparking.Smartparking.service.impl;

import com.smartparking.Smartparking.dto.request.UserProfileRequestDto;
import com.smartparking.Smartparking.dto.response.UserProfileResponseDto;
import com.smartparking.Smartparking.entity.iam.User;
import com.smartparking.Smartparking.entity.iam.UserProfile;
import com.smartparking.Smartparking.repository.UserProfileRepository;
import com.smartparking.Smartparking.repository.UserRepository;
import com.smartparking.Smartparking.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserProfileResponseDto createUserProfile(String userId, UserProfileRequestDto profileRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        UserProfile profile = new UserProfile();
        profile.setProfileId(java.util.UUID.randomUUID().toString());
        profile.setFirstName(profileRequestDto.getFirstName());
        profile.setLastName(profileRequestDto.getLastName());
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());

        profile = userProfileRepository.save(profile);
        return convertToResponseDto(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponseDto findByUserId(String userId) {
        UserProfile profile = userProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user id: " + userId));
        return convertToResponseDto(profile);
    }

    @Override
    @Transactional
    public UserProfileResponseDto updateUserProfile(String userId, UserProfileRequestDto profileRequestDto) {
        UserProfile profile = userProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user id: " + userId));

        profile.setFirstName(profileRequestDto.getFirstName());
        profile.setLastName(profileRequestDto.getLastName());
        profile.setUpdatedAt(LocalDateTime.now());

        profile = userProfileRepository.save(profile);
        return convertToResponseDto(profile);
    }

    @Override
    @Transactional
    public void deleteUserProfile(String userId) {
        userProfileRepository.deleteByUser_UserId(userId);
    }

    private UserProfileResponseDto convertToResponseDto(UserProfile profile) {
        UserProfileResponseDto dto = new UserProfileResponseDto();
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        return dto;
    }
}