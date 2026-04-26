package com.example.mentalhealth.gatewayservice.mapper;

import com.example.mentalhealth.gatewayservice.dto.gateway.GatewayAnswerRequest;
import com.example.mentalhealth.gatewayservice.dto.screening.ScreeningRequestDTO;
import com.example.mentalhealth.gatewayservice.dto.questionnaire.AnswerSubmissionDTO;
import com.example.mentalhealth.gatewayservice.session.GatewaySession;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GatewayDTOMapper {

    /*
     * Gateway → Screening Service
     */
    public ScreeningRequestDTO toScreeningRequest(
            GatewayAnswerRequest request,
            GatewaySession session
    ) {

        ScreeningRequestDTO dto = new ScreeningRequestDTO();

        dto.setSessionId(session.getScreeningSessionId());
        dto.setCorrelationId(session.getCorrelationId());
        dto.setMessage(request.getMessage());

        return dto;
    }

    /*
     * Gateway → Questionnaire Service
     */
    public AnswerSubmissionDTO toQuestionnaireRequest(
            GatewayAnswerRequest request,
            GatewaySession session
    ) {

        if (request.getAnswer() == null && request.getResponses() == null) {
            throw new IllegalArgumentException(
                    "Either answer or responses must be provided"
            );
        }

        AnswerSubmissionDTO dto = new AnswerSubmissionDTO();

        dto.setSessionId(session.getQuestionnaireSessionId());
        dto.setQuestionnaireId(session.getQuestionnaireId());
        dto.setQuestionId(request.getQuestionId());

        // Pass simple answer if present
        if (request.getAnswer() != null) {
            dto.setAnswer(request.getAnswer());
        }

        // Pass responses map if present
        if (request.getResponses() != null) {
            dto.setResponses(request.getResponses());
        }

        return dto;
    }
}