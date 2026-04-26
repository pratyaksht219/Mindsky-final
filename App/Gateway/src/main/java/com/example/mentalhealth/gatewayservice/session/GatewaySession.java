package com.example.mentalhealth.gatewayservice.session;

import lombok.Data;

@Data
public class GatewaySession {

    private String sessionId;

    private String screeningSessionId;

    private String questionnaireSessionId;

    private String questionnaireId;

    private String correlationId;

    private GatewayPhase phase;
}