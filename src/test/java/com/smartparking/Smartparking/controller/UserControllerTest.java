package com.smartparking.Smartparking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartparking.Smartparking.dto.request.LoginRequestDto;
import com.smartparking.Smartparking.dto.request.RegistrationRequestDto;
import com.smartparking.Smartparking.dto.request.UserRequestDto;
import com.smartparking.Smartparking.dto.response.LoginResponseDto;
import com.smartparking.Smartparking.dto.response.UserResponseDto;
import com.smartparking.Smartparking.entity.iam.User;
import com.smartparking.Smartparking.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("Tests para UserController - Gestión de Usuarios y Autenticación")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    // Mocks necesarios para la configuración de seguridad
    @MockBean
    private com.smartparking.Smartparking.config.JwtService jwtService;

    @MockBean
    private com.smartparking.Smartparking.config.JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @MockBean
    private com.smartparking.Smartparking.security.JwtUtil jwtUtil;

    @MockBean
    private com.smartparking.Smartparking.security.JwtFilter jwtFilter;

    private RegistrationRequestDto registrationRequestDto;
    private LoginRequestDto loginRequestDto;
    private UserResponseDto userResponseDto;
    private LoginResponseDto loginResponseDto;
    private UserRequestDto userRequestDto;
    private String userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        
        // Setup RegistrationRequestDto
        registrationRequestDto = new RegistrationRequestDto();
        registrationRequestDto.setFirstName("Juan");
        registrationRequestDto.setLastName("Pérez");
        registrationRequestDto.setEmail("juan.perez@upc.edu.pe");
        registrationRequestDto.setPassword("password123");

        // Setup LoginRequestDto
        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("juan.perez@upc.edu.pe");
        loginRequestDto.setPassword("password123");

        // Setup UserResponseDto
        userResponseDto = new UserResponseDto();
        userResponseDto.setUserId(userId);
        userResponseDto.setEmail("juan.perez@upc.edu.pe");
        userResponseDto.setRole("university_member");
        userResponseDto.setStatus("active");
        userResponseDto.setCreatedAt(LocalDateTime.now());

        // Setup LoginResponseDto
        loginResponseDto = new LoginResponseDto();
        loginResponseDto.setToken("mock-jwt-token");
        loginResponseDto.setSessionId(UUID.randomUUID().toString());
        loginResponseDto.setStatus(User.Status.active);

        // Setup UserRequestDto
        userRequestDto = new UserRequestDto();
        userRequestDto.setEmail("admin@upc.edu.pe");
        userRequestDto.setPasswordHash("hashedPassword");
        userRequestDto.setRole(User.Role.administrator);
        userRequestDto.setStatus(User.Status.active);
    }

    @Test
    @DisplayName("POST /register/university-member - Debe registrar un miembro universitario exitosamente")
    void testRegisterUniversityMember_Success() throws Exception {
        when(userService.registerUniversityMember(any(RegistrationRequestDto.class)))
                .thenReturn(userResponseDto);

        mockMvc.perform(post("/api/v1/auth/users/register/university-member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.email").value("juan.perez@upc.edu.pe"))
                .andExpect(jsonPath("$.role").value("university_member"));

        verify(userService, times(1)).registerUniversityMember(any(RegistrationRequestDto.class));
    }

    @Test
    @DisplayName("POST /register/administrator - Debe registrar un administrador exitosamente")
    void testRegisterAdministrator_Success() throws Exception {
        UserResponseDto adminResponse = new UserResponseDto();
        adminResponse.setUserId(UUID.randomUUID().toString());
        adminResponse.setEmail("admin@upc.edu.pe");
        adminResponse.setRole("administrator");
        adminResponse.setStatus("active");

        when(userService.registerAdministrator(any(RegistrationRequestDto.class)))
                .thenReturn(adminResponse);

        mockMvc.perform(post("/api/v1/auth/users/register/administrator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.role").value("administrator"));

        verify(userService, times(1)).registerAdministrator(any(RegistrationRequestDto.class));
    }

    @Test
    @DisplayName("POST /login - Debe autenticar un usuario exitosamente")
    void testLogin_Success() throws Exception {
        when(userService.login(any(LoginRequestDto.class)))
                .thenReturn(loginResponseDto);

        mockMvc.perform(post("/api/v1/auth/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.sessionId").exists());

        verify(userService, times(1)).login(any(LoginRequestDto.class));
    }

    @Test
    @DisplayName("POST /login - Debe retornar error con credenciales inválidas")
    void testLogin_InvalidCredentials() throws Exception {
        when(userService.login(any(LoginRequestDto.class)))
                .thenThrow(new com.smartparking.Smartparking.exception.BadRequestException("Invalid password"));

        mockMvc.perform(post("/api/v1/auth/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).login(any(LoginRequestDto.class));
    }

    @Test
    @DisplayName("POST /logout/{sessionId} - Debe cerrar sesión exitosamente")
    void testLogout_Success() throws Exception {
        String sessionId = UUID.randomUUID().toString();
        doNothing().when(userService).logout(sessionId);

        mockMvc.perform(post("/api/v1/auth/users/logout/{sessionId}", sessionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).logout(sessionId);
    }

    @Test
    @DisplayName("GET /{userId} - Debe obtener un usuario por ID")
    void testGetUser_Success() throws Exception {
        when(userService.findById(userId)).thenReturn(userResponseDto);

        mockMvc.perform(get("/api/v1/auth/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.email").value("juan.perez@upc.edu.pe"));

        verify(userService, times(1)).findById(userId);
    }

    @Test
    @DisplayName("GET / - Debe obtener todos los usuarios")
    void testGetAllUsers_Success() throws Exception {
        List<UserResponseDto> users = Arrays.asList(userResponseDto);
        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/v1/auth/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].userId").exists());

        verify(userService, times(1)).findAll();
    }

    @Test
    @DisplayName("POST / - Debe crear un usuario")
    void testCreateUser_Success() throws Exception {
        when(userService.createUser(any(UserRequestDto.class)))
                .thenReturn(userResponseDto);

        mockMvc.perform(post("/api/v1/auth/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").exists());

        verify(userService, times(1)).createUser(any(UserRequestDto.class));
    }

    @Test
    @DisplayName("PUT /{userId} - Debe actualizar un usuario")
    void testUpdateUser_Success() throws Exception {
        when(userService.updateUser(eq(userId), any(UserRequestDto.class)))
                .thenReturn(userResponseDto);

        mockMvc.perform(put("/api/v1/auth/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").exists());

        verify(userService, times(1)).updateUser(eq(userId), any(UserRequestDto.class));
    }

    @Test
    @DisplayName("DELETE /{userId} - Debe eliminar un usuario")
    void testDeleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/v1/auth/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(userId);
    }
}
