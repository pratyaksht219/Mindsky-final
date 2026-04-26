package com.example.mentalhealth.gatewayservice.clients;

import com.example.mentalhealth.gatewayservice.dto.screening.ScreeningRequestDTO;
import com.example.mentalhealth.gatewayservice.dto.screening.ScreeningResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ScreeningServiceClient {

    private final WebClient.Builder webClient;

//    private final String SCREENING_BASE_URL = "http://screening:8081";
    private static final String SCREENING_BASE_URL = "http://localhost:8081";

    public ScreeningResponseDTO startScreening(String correlationId) {

        return webClient.build()
                .post()
                .uri(SCREENING_BASE_URL + "/api/screening/start")
                .header("X-Correlation-ID", correlationId)
                .retrieve()
                .bodyToMono(ScreeningResponseDTO.class)
                .block();
    }

    public ScreeningResponseDTO submitAnswer(ScreeningRequestDTO request) {

        return webClient.build()
                .post()
                .uri(SCREENING_BASE_URL + "/api/screening/answer")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ScreeningResponseDTO.class)
                .block();
    }
}