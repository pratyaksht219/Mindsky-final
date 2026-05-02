package com.example.mentalhealth.aiService.implementation;

import com.example.mentalhealth.Assessment.AssessmentBreakdown;
import com.example.mentalhealth.Assessment.AssessmentResult;
import com.example.mentalhealth.aiService.AIService;
import com.example.mentalhealth.aiService.DTO.AIServiceRequestDTO;
import com.example.mentalhealth.aiService.DTO.AIServiceResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Slf4j
@Service
public class AIServiceImplementation implements AIService {

    private final WebClient webClient;

    public AIServiceImplementation(
            WebClient.Builder builder,
            @Value("${ai.service.url}") String aiServiceUrl
    ) {
        this.webClient = builder
                .baseUrl(aiServiceUrl)
                .build();

        log.info("AI client initialized with URL: {}", aiServiceUrl);
    }

    @Override
    public AIServiceResponseDTO getAiServiceResponse(AIServiceRequestDTO requestDTO) {

        log.info("AI Service called with request: {}", requestDTO);

        return webClient.post()
                .uri("/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.bodyToMono(String.class)
                                .map(msg -> new RuntimeException("AI Service error: " + msg))
                )
                .bodyToMono(AIServiceResponseDTO.class)
                .block(Duration.ofSeconds(60));
    }
}