package com.example.mentalhealth.gatewayservice.dto.screening;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreeningResponseDTO {

    /** Current phase of screening */
    private String phase;

    /** Screening session ID */
    private String sessionId;

    /** Next question to ask (if screening continues) */
    private QuestionDTO question;

    /** For maintaining correlation among the different microservices */
    private String correlationId;

    /** Decision returned when screening completes */
    private ScreeningDecisionDTO decision;

    /** Message used for crisis responses */
    private String message;
}