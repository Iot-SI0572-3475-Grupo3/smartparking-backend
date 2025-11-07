package com.smartparking.Smartparking.controller.analytics;

import com.smartparking.Smartparking.dto.response.analytics.DashboardResponse;
import com.smartparking.Smartparking.service.analytics.DashboardService;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1/reservation/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DashboardResponse> getDashboard() {
        String userId = getCurrentUserId();
        DashboardResponse response = dashboardService.getUserDashboard(userId);
        return ResponseEntity.ok(response);
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof String) {
            return (String) auth.getPrincipal();
        }
        throw new IllegalStateException("User not authenticated");
    }
}