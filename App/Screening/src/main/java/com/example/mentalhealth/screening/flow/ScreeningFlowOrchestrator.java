package com.example.mentalhealth.screening.flow;

import com.example.mentalhealth.screening.dto.ScreeningRequestDTO;
import com.example.mentalhealth.screening.dto.ScreeningResponseDTO;

public interface ScreeningFlowOrchestrator {

    ScreeningResponseDTO startScreening(String correlationId);

    ScreeningResponseDTO submitAnswer(ScreeningRequestDTO request);
}