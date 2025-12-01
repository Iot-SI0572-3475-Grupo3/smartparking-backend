package com.smartparking.Smartparking.service.space_iot;

import com.smartparking.Smartparking.dto.request.space_iot.ParkingSpaceRequestDto;
import com.smartparking.Smartparking.dto.request.space_iot.UpdateParkingSpaceDto;
import com.smartparking.Smartparking.dto.response.space_iot.ParkingSpaceResponse;
import com.smartparking.Smartparking.dto.response.space_iot.ParkingSpaceStatusResponse;
import com.smartparking.Smartparking.entity.space_iot.ParkingSpace;

import java.util.List;
import java.util.Optional;

public interface ParkingSpaceService {
    List<ParkingSpaceResponse> getAllParkingSpaces();
    List<ParkingSpaceResponse> getParkingSpacesByStatus(String status);

    ParkingSpaceResponse createParkingSpace(ParkingSpaceRequestDto request);
    ParkingSpaceResponse updateParkingSpace(String spaceId, UpdateParkingSpaceDto request);

    ParkingSpaceStatusResponse getStatusByCode(String code);

}