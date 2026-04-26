package com.example.mentalhealth.aiService.implementation;

import com.example.mentalhealth.Assessment.AssessmentBreakdown;
import com.example.mentalhealth.Assessment.AssessmentResult;
import com.example.mentalhealth.aiService.AIService;
import com.example.mentalhealth.aiService.DTO.AIServiceRequestDTO;
import com.example.mentalhealth.aiService.DTO.AIServiceResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Service
public class AIServiceImplementation implements AIService {

//private static final String AI_SERVICE_URL = "http://ai:8000/analyze";
     private static final String AI_SERVICE_URL = "http://localhost:8000/analyze";

    @Autowired
    private WebClient.Builder builder;

    private WebClient getAiClient(){
        return builder.build();
    }


    @Override
    public AIServiceResponseDTO getAiServiceResponse(AIServiceRequestDTO requestDTO) {
        System.out.println("AI Service called"+requestDTO);
        return getAiClient()
                .post()
                .uri(AI_SERVICE_URL)
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
