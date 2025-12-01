package com.smartparking.Smartparking.controller.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartparking.Smartparking.dto.request.reservation.CancelReservationRequest;
import com.smartparking.Smartparking.dto.request.reservation.ReservationRequestDto;
import com.smartparking.Smartparking.dto.response.reservation.ActiveReservationResponse;
import com.smartparking.Smartparking.dto.response.reservation.ReservationHistoryResponse;
import com.smartparking.Smartparking.dto.response.reservation.ReservationResponse;
import com.smartparking.Smartparking.entity.reservation.Reservation;
import com.smartparking.Smartparking.entity.space_iot.ParkingSpace;
import com.smartparking.Smartparking.repository.reservation.ReservationRepository;
import com.smartparking.Smartparking.repository.space_iot.ParkingSpaceRepository;
import com.smartparking.Smartparking.service.reservation.ReservationService;
import com.smartparking.Smartparking.service.space_iot.ParkingSpaceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("Tests para ReservationController - Gestión de Reservas")
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private ParkingSpaceService parkingSpaceService;

    @MockBean
    private ParkingSpaceRepository parkingSpaceRepository;

    @MockBean
    private ReservationRepository reservationRepository;

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

    private ReservationRequestDto reservationRequestDto;
    private ReservationResponse reservationResponse;
    private ActiveReservationResponse activeReservationResponse;
    private ReservationHistoryResponse historyResponse;
    private String userId;
    private String reservationId;
    private String spaceId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        reservationId = UUID.randomUUID().toString();
        spaceId = UUID.randomUUID().toString();
        
        // Configurar SecurityContext con el userId como principal
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userId, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Setup ReservationRequestDto
        reservationRequestDto = ReservationRequestDto.builder()
                .spaceId(spaceId)
                .userId(userId)
                .startTime(LocalDateTime.now().plusHours(1))
                .vehicleInfo("{\"plate\":\"ABC123\",\"model\":\"Toyota\"}")
                .specialRequirements("Acceso para discapacitados")
                .build();

        // Setup ReservationResponse
        reservationResponse = ReservationResponse.builder()
                .reservationId(reservationId)
                .spaceCode("PARK-001")
                .startTime(LocalDateTime.now().plusHours(1))
                .endTime(null)
                .date(LocalDateTime.now())
                .status("pending")
                .vehicleInfo("{\"plate\":\"ABC123\"}")
                .specialRequirements("Acceso para discapacitados")
                .totalCost(BigDecimal.ZERO)
                .paymentStatus(null)
                .createdAt(LocalDateTime.now())
                .build();

        // Setup ActiveReservationResponse
        activeReservationResponse = ActiveReservationResponse.builder()
                .reservationId(reservationId)
                .spaceCode("PARK-001")
                .startTime(LocalDateTime.now())
                .status("active")
                .build();

        // Setup HistoryResponse
        historyResponse = ReservationHistoryResponse.builder()
                .reservationId(reservationId)
                .spaceCode("PARK-001")
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().minusDays(1).plusHours(2))
                .status("completed")
                .totalCost(new BigDecimal("10.50"))
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("POST / - Debe crear una reserva exitosamente")
    void testCreateReservation_Success() throws Exception {
        when(reservationService.createReservation(any(ReservationRequestDto.class)))
                .thenReturn(reservationResponse);

        mockMvc.perform(post("/api/v1/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reservationId").exists())
                .andExpect(jsonPath("$.spaceCode").value("PARK-001"))
                .andExpect(jsonPath("$.status").value("pending"));

        verify(reservationService, times(1)).createReservation(any(ReservationRequestDto.class));
    }

    @Test
    @DisplayName("GET /history - Debe obtener el historial de reservas del usuario")
    void testGetReservationHistory_Success() throws Exception {
        List<ReservationHistoryResponse> history = Arrays.asList(historyResponse);
        when(reservationService.getReservationHistory(userId)).thenReturn(history);

        mockMvc.perform(get("/api/v1/reservation/history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].reservationId").exists())
                .andExpect(jsonPath("$[0].status").value("completed"));

        verify(reservationService, times(1)).getReservationHistory(userId);
    }

    @Test
    @DisplayName("GET /active - Debe obtener la reserva activa del usuario")
    void testGetActiveReservation_Success() throws Exception {
        when(reservationService.getActiveReservation(userId))
                .thenReturn(Optional.of(activeReservationResponse));

        mockMvc.perform(get("/api/v1/reservation/active")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reservationId").exists())
                .andExpect(jsonPath("$.status").value("active"));

        verify(reservationService, times(1)).getActiveReservation(userId);
    }

    @Test
    @DisplayName("GET /active - Debe retornar 204 cuando no hay reserva activa")
    void testGetActiveReservation_NoContent() throws Exception {
        when(reservationService.getActiveReservation(userId))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/reservation/active")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(reservationService, times(1)).getActiveReservation(userId);
    }

    @Test
    @DisplayName("POST /{reservationId}/cancel - Debe cancelar una reserva exitosamente")
    void testCancelReservation_Success() throws Exception {
        CancelReservationRequest cancelRequest = new CancelReservationRequest();
        cancelRequest.setReason("Cambio de planes");

        Reservation cancelledReservation = new Reservation();
        cancelledReservation.setReservationId(reservationId);
        cancelledReservation.setStatus(Reservation.ReservationStatus.cancelled);
        ParkingSpace space = new ParkingSpace();
        space.setCode("PARK-001");
        cancelledReservation.setParkingSpace(space);

        when(reservationService.cancelReservation(eq(reservationId), eq(userId), any(CancelReservationRequest.class)))
                .thenReturn(cancelledReservation);

        mockMvc.perform(post("/api/v1/reservation/{reservationId}/cancel", reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reservationId").value(reservationId));

        verify(reservationService, times(1))
                .cancelReservation(eq(reservationId), eq(userId), any(CancelReservationRequest.class));
    }

    @Test
    @DisplayName("POST /{reservationId}/confirm - Debe confirmar una reserva exitosamente")
    void testConfirmReservation_Success() throws Exception {
        Reservation confirmedReservation = new Reservation();
        confirmedReservation.setReservationId(reservationId);
        confirmedReservation.setStatus(Reservation.ReservationStatus.confirmed);
        ParkingSpace space = new ParkingSpace();
        space.setCode("PARK-001");
        confirmedReservation.setParkingSpace(space);

        when(reservationService.confirmReservation(reservationId, userId))
                .thenReturn(confirmedReservation);

        mockMvc.perform(post("/api/v1/reservation/{reservationId}/confirm", reservationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reservationId").value(reservationId));

        verify(reservationService, times(1)).confirmReservation(reservationId, userId);
    }

    @Test
    @DisplayName("POST /{reservationId}/activate - Debe activar una reserva exitosamente")
    void testActivateReservation_Success() throws Exception {
        Reservation activatedReservation = new Reservation();
        activatedReservation.setReservationId(reservationId);
        activatedReservation.setStatus(Reservation.ReservationStatus.active);
        ParkingSpace space = new ParkingSpace();
        space.setCode("PARK-001");
        activatedReservation.setParkingSpace(space);

        when(reservationService.activateReservation(reservationId))
                .thenReturn(activatedReservation);

        mockMvc.perform(post("/api/v1/reservation/{reservationId}/activate", reservationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reservationId").value(reservationId));

        verify(reservationService, times(1)).activateReservation(reservationId);
    }

    @Test
    @DisplayName("GET /all-history - Debe obtener todo el historial de reservas")
    void testGetAllReservationHistory_Success() throws Exception {
        List<ReservationHistoryResponse> allHistory = Arrays.asList(historyResponse);
        when(reservationService.getAllReservationHistory()).thenReturn(allHistory);

        mockMvc.perform(get("/api/v1/reservation/all-history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].reservationId").exists());

        verify(reservationService, times(1)).getAllReservationHistory();
    }

    @Test
    @DisplayName("GET /parking-spaces-reservations/{code} - Debe obtener reservas por código de espacio")
    void testGetReservationsByCode_Success() throws Exception {
        String spaceCode = "PARK-001";
        List<ReservationResponse> reservations = Arrays.asList(reservationResponse);
        when(reservationService.getReservationsByParkingSpaceCode(spaceCode))
                .thenReturn(reservations);

        mockMvc.perform(get("/api/v1/reservation/parking-spaces-reservations/{code}", spaceCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].spaceCode").value(spaceCode));

        verify(reservationService, times(1)).getReservationsByParkingSpaceCode(spaceCode);
    }
}
