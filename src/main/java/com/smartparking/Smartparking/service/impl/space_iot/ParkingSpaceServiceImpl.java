package com.smartparking.Smartparking.service.impl.space_iot;

import com.smartparking.Smartparking.dto.request.space_iot.ParkingSpaceRequestDto;
import com.smartparking.Smartparking.dto.request.space_iot.UpdateParkingSpaceDto;
import com.smartparking.Smartparking.dto.response.space_iot.ParkingSpaceResponse;
import com.smartparking.Smartparking.entity.space_iot.ParkingSpace;
import com.smartparking.Smartparking.repository.space_iot.ParkingSpaceRepository;
import com.smartparking.Smartparking.service.space_iot.ParkingSpaceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingSpaceServiceImpl implements ParkingSpaceService {

    private final ParkingSpaceRepository parkingSpaceRepository;

    @Override
    public List<ParkingSpaceResponse> getAllParkingSpaces() {
        return parkingSpaceRepository.findAllOrderedByCode().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ParkingSpaceResponse> getParkingSpacesByStatus(String status) {
        ParkingSpace.SpaceStatus enumStatus = parseStatus(status);
        return parkingSpaceRepository.findByStatus(enumStatus).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public ParkingSpaceResponse createParkingSpace(ParkingSpaceRequestDto request) {
        if (parkingSpaceRepository.existsByCode(request.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El código ya está en uso");
        }

        ParkingSpace space = new ParkingSpace();
        space.setCode(request.getCode().toUpperCase());
        space.setStatus(parseStatus(request.getStatus(), ParkingSpace.SpaceStatus.available));
        space.setLastUpdated(LocalDateTime.now());

        space = parkingSpaceRepository.save(space);
        return mapToResponse(space);
    }

    @Override
    @Transactional
    public ParkingSpaceResponse updateParkingSpace(String spaceId, UpdateParkingSpaceDto request) {
        ParkingSpace space = parkingSpaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Espacio no encontrado"));

        if (request.getCode() != null && !request.getCode().isBlank()) {
            String newCode = request.getCode().toUpperCase();
            if (!newCode.equals(space.getCode()) && parkingSpaceRepository.existsByCode(newCode)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El código ya está en uso");
            }
            space.setCode(newCode);
        }

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            space.setStatus(parseStatus(request.getStatus(), space.getStatus()));
        }

        space.setLastUpdated(LocalDateTime.now());
        space = parkingSpaceRepository.save(space);
        return mapToResponse(space);
    }

    private ParkingSpace.SpaceStatus parseStatus(String status) {
        if (status == null || status.isBlank()) return null;
        try {
            return ParkingSpace.SpaceStatus.valueOf(status.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado inválido: " + status);
        }
    }

    private ParkingSpace.SpaceStatus parseStatus(String status, ParkingSpace.SpaceStatus defaultStatus) {
        return status == null || status.isBlank() ? defaultStatus : parseStatus(status);
    }

    private ParkingSpaceResponse mapToResponse(ParkingSpace ps) {
        return ParkingSpaceResponse.builder()
                .spaceId(ps.getSpaceId())
                .code(ps.getCode())
                .status(ps.getStatus().name().toLowerCase())
                .currentReservationId(ps.getCurrentReservationId())
                .lastUpdated(ps.getLastUpdated())
                .createdAt(ps.getCreatedAt())
                .build();
    }
}