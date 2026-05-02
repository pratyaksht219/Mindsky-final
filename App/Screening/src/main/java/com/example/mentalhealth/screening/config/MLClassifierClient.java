package com.example.mentalhealth.screening.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
public class MLClassifierClient {

    private final WebClient webClient;

    public MLClassifierClient(
            WebClient.Builder builder,
            @Value("${classifier.service.url}") String classifierBaseUrl
    ) {
        this.webClient = builder
                .baseUrl(classifierBaseUrl)
                .build();

        log.info("ML Classifier Client initialized with URL: {}", classifierBaseUrl);
    }

    public Map<String, Double> classify(String message) {

        Map<String, String> request = Map.of(
                "text", message
        );

        Map<String, Object> response = webClient.post()
                .uri("/classify")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        log.info("ML classifier scores: {}", response);

        return (Map<String, Double>) response.get("scores");
    }
}