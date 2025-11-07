package com.smartparking.Smartparking.service.analytics;


import com.smartparking.Smartparking.dto.response.analytics.DashboardResponse;

public interface DashboardService {
    DashboardResponse getUserDashboard(String userId);
}