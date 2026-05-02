package com.example.mentalhealth.gatewayservice.clients;

import com.example.mentalhealth.gatewayservice.dto.screening.ScreeningRequestDTO;
import com.example.mentalhealth.gatewayservice.dto.screening.ScreeningResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class ScreeningServiceClient {

    private final WebClient webClient;

    public ScreeningServiceClient(
            WebClient.Builder builder,
            @Value("${screening.service.url}") String baseUrl
    ) {
        this.webClient = builder
                .baseUrl(baseUrl)
                .build();

        log.info("Screening client initialized with URL: {}", baseUrl);
    }

    public ScreeningResponseDTO startScreening(String correlationId) {

        return webClient.post()
                .uri("/api/screening/start")
                .header("X-Correlation-ID", correlationId)
                .retrieve()
                .bodyToMono(ScreeningResponseDTO.class)
                .block();
    }

    public ScreeningResponseDTO submitAnswer(ScreeningRequestDTO request) {

        return webClient.post()
                .uri("/api/screening/answer")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ScreeningResponseDTO.class)
                .block();
    }
}