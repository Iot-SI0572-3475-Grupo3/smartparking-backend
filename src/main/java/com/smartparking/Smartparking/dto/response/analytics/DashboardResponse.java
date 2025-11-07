package com.smartparking.Smartparking.dto.response.analytics;

import com.smartparking.Smartparking.dto.response.iam.SessionResponse;
import com.smartparking.Smartparking.dto.response.space_iot.ParkingSpaceResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private List<ParkingSpaceResponse> availableSpaces;
    private List<SessionResponse> recentSessions;
    private int absenceCount;
    private boolean canReserve;
}