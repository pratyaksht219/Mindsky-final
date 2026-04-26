package com.example.mentalhealth.gatewayservice.service;

import com.example.mentalhealth.gatewayservice.clients.QuestionnaireServiceClient;
import com.example.mentalhealth.gatewayservice.clients.ScreeningServiceClient;
import com.example.mentalhealth.gatewayservice.dto.gateway.GatewayAnswerRequest;
import com.example.mentalhealth.gatewayservice.dto.gateway.GatewayStartRequest;
import com.example.mentalhealth.gatewayservice.dto.questionnaire.AnswerSubmissionDTO;
import com.example.mentalhealth.gatewayservice.dto.questionnaire.FinalResponseDTO;
import com.example.mentalhealth.gatewayservice.dto.questionnaire.StartAssessmentRequest;
import com.example.mentalhealth.gatewayservice.dto.screening.ScreeningRequestDTO;
import com.example.mentalhealth.gatewayservice.dto.screening.ScreeningResponseDTO;
import com.example.mentalhealth.gatewayservice.mapper.GatewayDTOMapper;
import com.example.mentalhealth.gatewayservice.session.GatewayPhase;
import com.example.mentalhealth.gatewayservice.session.GatewaySession;
import com.example.mentalhealth.gatewayservice.session.GatewaySessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssessmentGatewayServiceImpl implements AssessmentGatewayService {

    private final ScreeningServiceClient screeningClient;
    private final QuestionnaireServiceClient questionnaireClient;
    private final GatewaySessionRegistry sessionRegistry;
    private final GatewayDTOMapper mapper;

    /**
     * Start entire assessment flow
     */
    @Override
    public ResponseEntity<Object> startFlow(GatewayStartRequest request) {

        ScreeningResponseDTO response =
                screeningClient.startScreening(request.getCorrelationId());

        GatewaySession session =
                sessionRegistry.create(response.getSessionId());

        session.setCorrelationId(request.getCorrelationId());

        log.info("Gateway session created | sessionId={}", session.getSessionId());

        response.setSessionId(session.getSessionId());
        return ResponseEntity.ok(response);
    }

    /**
     * Continue flow (screening OR questionnaire)
     */
    @Override
    public ResponseEntity<Object> continueFlow(GatewayAnswerRequest request) {

        GatewaySession session =
                sessionRegistry.get(request.getSessionId());

        if (session == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Session not found");
        }

        /*
         SCREENING PHASE
         */
        if (session.getPhase() == GatewayPhase.SCREENING) {

            ScreeningRequestDTO screeningRequest =
                    mapper.toScreeningRequest(request, session);

            ScreeningResponseDTO response =
                    screeningClient.submitAnswer(screeningRequest);

            if ("SCREENING_COMPLETED".equals(response.getPhase())) {

                String questionnaireId =
                        response.getDecision().getQuestionnaireId();

                log.info("Screening completed → routing to questionnaire {}", questionnaireId);

                StartAssessmentRequest startRequest =
                        new StartAssessmentRequest();

                startRequest.setQuestionnaireId(questionnaireId);

                FinalResponseDTO questionnaireResponse =
                        questionnaireClient.startAssessment(startRequest);

                session.setPhase(GatewayPhase.QUESTIONNAIRE);
                session.setQuestionnaireSessionId(
                        questionnaireResponse.getSessionId()
                );
                session.setQuestionnaireId(questionnaireId);

                return ResponseEntity.ok(questionnaireResponse);
            }

            return ResponseEntity.ok(response);
        }

        /*
         QUESTIONNAIRE PHASE
         */
        if (session.getPhase() == GatewayPhase.QUESTIONNAIRE) {

            AnswerSubmissionDTO questionnaireRequest =
                    mapper.toQuestionnaireRequest(request, session);

            FinalResponseDTO response =
                    questionnaireClient.submitAnswer(questionnaireRequest);

            if ("COMPLETED".equals(response.getPhase())) {

                log.info("Assessment completed | sessionId={}", session.getSessionId());

                session.setPhase(GatewayPhase.COMPLETED);
                sessionRegistry.remove(session.getSessionId());
            }

            return ResponseEntity.ok(response);
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid gateway phase");
    }
}