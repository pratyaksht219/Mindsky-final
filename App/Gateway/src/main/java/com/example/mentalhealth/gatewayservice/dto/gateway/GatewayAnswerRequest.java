package com.example.mentalhealth.gatewayservice.dto.gateway;

import lombok.Data;

import java.util.Map;

@Data
public class GatewayAnswerRequest {

    private String sessionId;

    private String message;

    private String questionId;

    private Integer answer;

    private String correlationId;

    private Map<String, Object> responses;


}