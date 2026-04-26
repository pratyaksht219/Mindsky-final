package com.example.mentalhealth.emergency_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationRequestDTO {
    private double latitude;
    private double longitude;
}
