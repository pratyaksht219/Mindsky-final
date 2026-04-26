package com.example.mentalhealth.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private static final String SCREENING_SERVICE_BASE_URL = "http://localhost:8081";
    private static final String QUESTIONNAIRE_SERVICE_BASE_URL = "http://localhost:8082";

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient screeningWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(SCREENING_SERVICE_BASE_URL)
                .build();
    }

    @Bean
    public WebClient questionnaireWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(QUESTIONNAIRE_SERVICE_BASE_URL)
                .build();
    }

}