package com.example.mentalhealth.screening.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
@Slf4j
@Service
public class MLClassifierClient {

    private final WebClient webClient;
//    public static final String CLASSIFIER_CLIENT_URL = "http://classifier:9000";
    public static final String CLASSIFIER_CLIENT_URL = "http://localhost:9000";

    public MLClassifierClient(WebClient.Builder builder) {

        this.webClient = builder
                .baseUrl(CLASSIFIER_CLIENT_URL)
                .build();
    }

    public Map<String, Double> classify(String message) {

        Map<String, String> request =
                Map.of("text", message);

        Map<String, Object> response =
                webClient.post()
                        .uri("/classify")
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();
        log.info("ML classifier scores: {}", response);

        return (Map<String, Double>) response.get("scores");
    }
}