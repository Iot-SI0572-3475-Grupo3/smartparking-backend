package com.smartparking.Smartparking.service.space_iot;

import com.smartparking.Smartparking.dto.request.space_iot.ParkingSpaceRequestDto;
import com.smartparking.Smartparking.dto.request.space_iot.UpdateParkingSpaceDto;
import com.smartparking.Smartparking.dto.response.space_iot.ParkingSpaceResponse;

import java.util.List;

public interface ParkingSpaceService {
    List<ParkingSpaceResponse> getAllParkingSpaces();
    List<ParkingSpaceResponse> getParkingSpacesByStatus(String status);

    ParkingSpaceResponse createParkingSpace(ParkingSpaceRequestDto request);
    ParkingSpaceResponse updateParkingSpace(String spaceId, UpdateParkingSpaceDto request);
}