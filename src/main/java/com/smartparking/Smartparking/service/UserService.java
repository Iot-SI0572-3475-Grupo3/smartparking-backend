package com.smartparking.Smartparking.service;

import com.smartparking.Smartparking.dto.request.LoginRequestDto;
import com.smartparking.Smartparking.dto.request.RegistrationRequestDto;
import com.smartparking.Smartparking.dto.request.UserRequestDto;
import com.smartparking.Smartparking.dto.response.LoginResponseDto;
import com.smartparking.Smartparking.dto.response.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequestDto);
    UserResponseDto registerUniversityMember(RegistrationRequestDto registrationRequestDto);
    UserResponseDto registerAdministrator(RegistrationRequestDto registrationRequestDto);
    LoginResponseDto login(LoginRequestDto loginRequestDto);
    void logout(String sessionId);
    UserResponseDto findById(String userId);
    List<UserResponseDto> findAll();
    UserResponseDto updateUser(String userId, UserRequestDto userRequestDto);
    void deleteUser(String userId);
}