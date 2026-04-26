package com.example.mentalhealth.gatewayservice.clients;

import com.example.mentalhealth.gatewayservice.dto.questionnaire.AnswerSubmissionDTO;
import com.example.mentalhealth.gatewayservice.dto.questionnaire.FinalResponseDTO;
import com.example.mentalhealth.gatewayservice.dto.questionnaire.StartAssessmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class QuestionnaireServiceClient {

    private final WebClient.Builder webClient;

    private static final String QUESTIONNAIRE_BASE_URL = "http://localhost:8082";
//    private static final String QUESTIONNAIRE_BASE_URL = "http://questionnaire:8082";


    public FinalResponseDTO startAssessment(StartAssessmentRequest request) {

        return webClient.build()
                .post()
                .uri(QUESTIONNAIRE_BASE_URL + "/api/assessment/start")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(FinalResponseDTO.class)
                .block();
    }

    public FinalResponseDTO submitAnswer(AnswerSubmissionDTO request) {

        return webClient.build()
                .post()
                .uri(QUESTIONNAIRE_BASE_URL + "/api/assessment/answer")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(FinalResponseDTO.class)
                .block();
    }
}