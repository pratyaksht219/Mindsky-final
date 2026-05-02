package com.example.mentalhealth.gatewayservice.clients;

import com.example.mentalhealth.gatewayservice.dto.questionnaire.AnswerSubmissionDTO;
import com.example.mentalhealth.gatewayservice.dto.questionnaire.FinalResponseDTO;
import com.example.mentalhealth.gatewayservice.dto.questionnaire.StartAssessmentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class QuestionnaireServiceClient {

    private final WebClient webClient;

    public QuestionnaireServiceClient(
            WebClient.Builder builder,
            @Value("${questionnaire.service.url}") String baseUrl
    ) {
        this.webClient = builder
                .baseUrl(baseUrl)
                .build();

        log.info("Questionnaire client initialized with URL: {}", baseUrl);
    }

    public FinalResponseDTO startAssessment(StartAssessmentRequest request) {

        return webClient.post()
                .uri("/api/assessment/start")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(FinalResponseDTO.class)
                .block();
    }

    public FinalResponseDTO submitAnswer(AnswerSubmissionDTO request) {

        return webClient.post()
                .uri("/api/assessment/answer")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(FinalResponseDTO.class)
                .block();
    }
}