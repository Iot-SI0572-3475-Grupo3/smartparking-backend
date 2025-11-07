package com.smartparking.Smartparking.dto.response.iam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {
    private String spaceCode;
    private String date;
    private String start;
    private String end;
}