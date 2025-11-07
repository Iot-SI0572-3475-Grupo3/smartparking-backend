package com.smartparking.Smartparking.service.impl;

import com.smartparking.Smartparking.dto.request.LoginRequestDto;
import com.smartparking.Smartparking.dto.request.RegistrationRequestDto;
import com.smartparking.Smartparking.dto.request.UserRequestDto;
import com.smartparking.Smartparking.dto.response.LoginResponseDto;
import com.smartparking.Smartparking.dto.response.UserResponseDto;
import com.smartparking.Smartparking.entity.iam.User;
import com.smartparking.Smartparking.entity.iam.UserProfile;
import com.smartparking.Smartparking.entity.iam.UserSession;
import com.smartparking.Smartparking.repository.UserProfileRepository;
import com.smartparking.Smartparking.repository.UserRepository;
import com.smartparking.Smartparking.repository.UserSessionRepository;
import com.smartparking.Smartparking.security.Argon2PasswordEncoder;
import com.smartparking.Smartparking.security.JwtUtil;
import com.smartparking.Smartparking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private Argon2PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.secret}") // Definir en application.properties
    private String jwtSecret;

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = new User();
        user.setUserId(java.util.UUID.randomUUID().toString());
        user.setEmail(userRequestDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userRequestDto.getPasswordHash()));
        user.setRole(userRequestDto.getRole());
        user.setStatus(userRequestDto.getStatus());
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLoginAt(null);
        user.setFailedLoginAttempt(0);

        user = userRepository.save(user);
        return convertToResponseDto(user);
    }

    @Transactional
    public UserResponseDto registerUniversityMember(RegistrationRequestDto registrationRequestDto) {
        User user = new User();
        user.setUserId(java.util.UUID.randomUUID().toString());
        user.setEmail(registrationRequestDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registrationRequestDto.getPassword()));
        user.setRole(User.Role.university_member);
        user.setStatus(User.Status.active);
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLoginAt(null);
        user.setFailedLoginAttempt(0);

        user = userRepository.save(user);

        LocalDateTime now = LocalDateTime.now();
        UserProfile profile = new UserProfile(
                registrationRequestDto.getFirstName(),
                registrationRequestDto.getLastName(),
                now,
                now
        );
        profile.setUser(user);
        user.setProfile(profile);

        userRepository.save(user);

        return convertToResponseDto(user);
    }

    @Transactional
    public UserResponseDto registerAdministrator(RegistrationRequestDto registrationRequestDto) {
        User user = new User();
        user.setUserId(java.util.UUID.randomUUID().toString());
        user.setEmail(registrationRequestDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registrationRequestDto.getPassword()));
        user.setRole(User.Role.administrator);
        user.setStatus(User.Status.active);
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLoginAt(null);
        user.setFailedLoginAttempt(0);

        user = userRepository.save(user);

        LocalDateTime now = LocalDateTime.now();
        UserProfile profile = new UserProfile(
                registrationRequestDto.getFirstName(),
                registrationRequestDto.getLastName(),
                now,
                now
        );
        profile.setUser(user);
        user.setProfile(profile);

        userRepository.save(user);

        return convertToResponseDto(user);
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + loginRequestDto.getEmail()));

        // SIN TRUNCAR → Usamos Argon2 o BCrypt correctamente
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        // Generar JWT con userId y role
        String token = jwtUtil.generateToken(user.getUserId(), user.getRole().name());

        // Hashear el token para almacenarlo
        String tokenHash = passwordEncoder.encode(token);

        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);

        UserSession session = new UserSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setUserId(user.getUserId());
        session.setTokenHash(tokenHash);
        session.setExpiresAt(expiresAt);
        session.setIsActive(true);
        userSessionRepository.save(session);

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setSessionId(session.getSessionId());
        return response;
    }

    @Transactional
    public void logout(String sessionId) {
        UserSession session = userSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + sessionId));
        session.setIsActive(false);
        userSessionRepository.save(session);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return convertToResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(String userId, UserRequestDto userRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        user.setEmail(userRequestDto.getEmail());
        if (userRequestDto.getPasswordHash() != null && !userRequestDto.getPasswordHash().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(userRequestDto.getPasswordHash()));
        }
        user.setRole(userRequestDto.getRole());
        user.setStatus(userRequestDto.getStatus());

        user = userRepository.save(user);
        return convertToResponseDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    private UserResponseDto convertToResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setStatus(user.getStatus().name());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}