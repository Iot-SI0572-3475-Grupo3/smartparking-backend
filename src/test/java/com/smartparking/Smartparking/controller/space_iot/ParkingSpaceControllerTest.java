package com.smartparking.Smartparking.controller.space_iot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartparking.Smartparking.dto.request.space_iot.ParkingSpaceRequestDto;
import com.smartparking.Smartparking.dto.request.space_iot.UpdateParkingSpaceDto;
import com.smartparking.Smartparking.dto.response.space_iot.ParkingSpaceResponse;
import com.smartparking.Smartparking.service.space_iot.ParkingSpaceService;
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

@WebMvcTest(controllers = ParkingSpaceController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("Tests para ParkingSpaceController - Gestión de Espacios de Estacionamiento")
class ParkingSpaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ParkingSpaceService parkingSpaceService;

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

    private ParkingSpaceRequestDto parkingSpaceRequestDto;
    private UpdateParkingSpaceDto updateParkingSpaceDto;
    private ParkingSpaceResponse parkingSpaceResponse;
    private String spaceId;

    @BeforeEach
    void setUp() {
        spaceId = UUID.randomUUID().toString();
        
        // Setup ParkingSpaceRequestDto - código válido según patrón ^[A-Z0-9]+$
        parkingSpaceRequestDto = new ParkingSpaceRequestDto();
        parkingSpaceRequestDto.setCode("PARK001");
        parkingSpaceRequestDto.setStatus("available");

        // Setup UpdateParkingSpaceDto
        updateParkingSpaceDto = new UpdateParkingSpaceDto();
        updateParkingSpaceDto.setStatus("maintenance");

        // Setup ParkingSpaceResponse
        parkingSpaceResponse = ParkingSpaceResponse.builder()
                .spaceId(spaceId)
                .code("PARK001")
                .status("available")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("GET / - Debe obtener todos los espacios de estacionamiento")
    void testGetAllParkingSpaces_Success() throws Exception {
        List<ParkingSpaceResponse> spaces = Arrays.asList(parkingSpaceResponse);
        when(parkingSpaceService.getAllParkingSpaces()).thenReturn(spaces);

        mockMvc.perform(get("/api/v1/space-iot/parking-spaces")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].code").value("PARK001"))
                .andExpect(jsonPath("$[0].status").value("available"));

        verify(parkingSpaceService, times(1)).getAllParkingSpaces();
    }

    @Test
    @DisplayName("GET /status/{status} - Debe obtener espacios filtrados por estado")
    void testGetParkingSpacesByStatus_Success() throws Exception {
        String status = "available";
        List<ParkingSpaceResponse> spaces = Arrays.asList(parkingSpaceResponse);
        when(parkingSpaceService.getParkingSpacesByStatus(status)).thenReturn(spaces);

        mockMvc.perform(get("/api/v1/space-iot/parking-spaces/status/{status}", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("available"));

        verify(parkingSpaceService, times(1)).getParkingSpacesByStatus(status);
    }

    @Test
    @DisplayName("POST / - Debe crear un espacio de estacionamiento exitosamente")
    void testCreateParkingSpace_Success() throws Exception {
        when(parkingSpaceService.createParkingSpace(any(ParkingSpaceRequestDto.class)))
                .thenReturn(parkingSpaceResponse);

        mockMvc.perform(post("/api/v1/space-iot/parking-spaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(parkingSpaceRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.spaceId").exists())
                .andExpect(jsonPath("$.code").value("PARK001"))
                .andExpect(jsonPath("$.status").value("available"));

        verify(parkingSpaceService, times(1)).createParkingSpace(any(ParkingSpaceRequestDto.class));
    }

    @Test
    @DisplayName("PUT /{spaceId} - Debe actualizar un espacio de estacionamiento")
    void testUpdateParkingSpace_Success() throws Exception {
        ParkingSpaceResponse updatedResponse = ParkingSpaceResponse.builder()
                .spaceId(spaceId)
                .code("PARK001")
                .status("maintenance")
                .createdAt(LocalDateTime.now())
                .build();

        when(parkingSpaceService.updateParkingSpace(eq(spaceId), any(UpdateParkingSpaceDto.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/space-iot/parking-spaces/{spaceId}", spaceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateParkingSpaceDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("maintenance"));

        verify(parkingSpaceService, times(1))
                .updateParkingSpace(eq(spaceId), any(UpdateParkingSpaceDto.class));
    }

    @Test
    @DisplayName("GET / - Debe retornar lista vacía cuando no hay espacios")
    void testGetAllParkingSpaces_EmptyList() throws Exception {
        when(parkingSpaceService.getAllParkingSpaces()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/space-iot/parking-spaces")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(parkingSpaceService, times(1)).getAllParkingSpaces();
    }
}
