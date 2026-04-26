package com.example.mentalhealth.screening.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreeningRequestDTO {

    @NotBlank(message = "Session ID is required")
    private String sessionId;

    private String correlationId;

    @NotBlank(message = "Message cannot be empty")
    @Size(max = 1000, message = "Message too long")
    private String message;
}