package com.smartparking.Smartparking.controller.space_iot;

import com.smartparking.Smartparking.dto.request.space_iot.ParkingSpaceRequestDto;
import com.smartparking.Smartparking.dto.request.space_iot.UpdateParkingSpaceDto;
import com.smartparking.Smartparking.dto.response.space_iot.ParkingSpaceResponse;
import com.smartparking.Smartparking.service.space_iot.ParkingSpaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/space-iot/parking-spaces")
@RequiredArgsConstructor
public class ParkingSpaceController {

    private final ParkingSpaceService parkingSpaceService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ParkingSpaceResponse>> getAllParkingSpaces() {
        return ResponseEntity.ok(parkingSpaceService.getAllParkingSpaces());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ParkingSpaceResponse>> getParkingSpacesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(parkingSpaceService.getParkingSpacesByStatus(status));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ParkingSpaceResponse> createParkingSpace(
            @Valid @RequestBody ParkingSpaceRequestDto request) {
        ParkingSpaceResponse response = parkingSpaceService.createParkingSpace(request);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{spaceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ParkingSpaceResponse> updateParkingSpace(
            @PathVariable String spaceId,
            @Valid @RequestBody UpdateParkingSpaceDto request) {
        ParkingSpaceResponse response = parkingSpaceService.updateParkingSpace(spaceId, request);
        return ResponseEntity.ok(response);
    }
}