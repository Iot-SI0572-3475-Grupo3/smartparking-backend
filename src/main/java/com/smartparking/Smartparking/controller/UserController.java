package com.smartparking.Smartparking.controller;

import com.smartparking.Smartparking.dto.request.LoginRequestDto;
import com.smartparking.Smartparking.dto.request.RegistrationRequestDto;
import com.smartparking.Smartparking.dto.request.UserRequestDto;
import com.smartparking.Smartparking.dto.response.LoginResponseDto;
import com.smartparking.Smartparking.dto.response.UserResponseDto;
import com.smartparking.Smartparking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto response = userService.createUser(userRequestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/university-member")
    public ResponseEntity<UserResponseDto> registerUniversityMember(@Valid @RequestBody RegistrationRequestDto registrationRequestDto) {
        UserResponseDto response = userService.registerUniversityMember(registrationRequestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/administrator")
    public ResponseEntity<UserResponseDto> registerAdministrator(@Valid @RequestBody RegistrationRequestDto registrationRequestDto) {
        UserResponseDto response = userService.registerAdministrator(registrationRequestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto response = userService.login(loginRequestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout/{sessionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(@PathVariable String sessionId) {
        userService.logout(sessionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable String userId) {
        UserResponseDto response = userService.findById(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable String userId, @Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto response = userService.updateUser(userId, userRequestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
