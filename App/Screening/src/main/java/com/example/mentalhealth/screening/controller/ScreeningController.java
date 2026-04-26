package com.example.mentalhealth.screening.controller;

import com.example.mentalhealth.screening.dto.ScreeningRequestDTO;
import com.example.mentalhealth.screening.dto.ScreeningResponseDTO;
import com.example.mentalhealth.screening.flow.ScreeningFlowOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/screening")
@RequiredArgsConstructor
public class ScreeningController {

    private final ScreeningFlowOrchestrator screeningFlowOrchestrator;

    @PostMapping("/start")
    public ScreeningResponseDTO startScreening(
            @RequestHeader(value = "X-Correlation-ID", required = false)
            String correlationId
    ) {

        log.info("Starting screening session. correlationId={}", correlationId);

        return screeningFlowOrchestrator.startScreening(correlationId);
    }

    @PostMapping("/answer")
    public ScreeningResponseDTO submitAnswer(
            @RequestBody ScreeningRequestDTO request
    ) {

        log.info("Received screening response. sessionId={}", request.getSessionId());


        return screeningFlowOrchestrator.submitAnswer(request);
    }
}