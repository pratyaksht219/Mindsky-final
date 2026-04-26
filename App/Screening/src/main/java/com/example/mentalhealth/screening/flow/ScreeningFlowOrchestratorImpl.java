package com.example.mentalhealth.screening.flow;

import com.example.mentalhealth.screening.config.MLClassifierClient;
import com.example.mentalhealth.screening.domain.*;
import com.example.mentalhealth.screening.dto.*;
import com.example.mentalhealth.screening.engine.*;
import com.example.mentalhealth.screening.engine.implementation.ScreeningQuestionSelector;
import com.example.mentalhealth.screening.repository.ScreeningSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScreeningFlowOrchestratorImpl implements ScreeningFlowOrchestrator {

    private final ScreeningSessionRepository sessionRepository;

//    private final KeywordExtractor keywordExtractor;
//    private final SignalMapper signalMapper;
    private final EmergencyDetector emergencyDetector;
    private final KeywordScreeningEngine screeningEngine;
    private final ScreeningQuestionSelector questionSelector;
    private final MLClassifierClient mlClassifierClient;
    private final MLSignalMapper mlSignalMapper;

    /**
     * Start screening
     */
    @Override
    public ScreeningResponseDTO startScreening(String correlationId) {

        ScreeningSession session =
                ScreeningSession.create(correlationId);

        sessionRepository.save(session);

        QuestionDTO firstQuestion =
                questionSelector.firstQuestion();

        return new ScreeningResponseDTO(
                "SCREENING_IN_PROGRESS",
                session.getSessionId(),
                firstQuestion,
                session.getCorrelationId(),
                null,
                null
        );
    }

    /**
     * Submit screening answer
     */
    @Override
    public ScreeningResponseDTO submitAnswer(ScreeningRequestDTO request) {

        ScreeningSession session =
                sessionRepository
                        .findById(request.getSessionId())
                        .orElseThrow(() ->
                                new IllegalStateException("Screening session not found"));

        if (session.isCompleted()) {
            throw new IllegalStateException("Screening already completed");
        }

        String correlationId =
                request.getCorrelationId() != null
                        ? request.getCorrelationId()
                        : session.getCorrelationId();

        session.incrementTurn();

        String message =
                request.getMessage() == null
                        ? ""
                        : request.getMessage();

        /* 1️⃣ Keyword extraction */

        Map<String, Double> scores =
                mlClassifierClient.classify(message);

        /* 2️⃣ Signal mapping */
        mlSignalMapper.map(
                scores,
                session.getSignalState()
        );


        /* 3️⃣ Emergency detection */

        EmergencyMatch emergency =
                emergencyDetector.detect(message);

        session.getSignalState()
                .escalateEmergency(emergency.getLevel());

        /* 4️⃣ Evaluate decision */

        ScreeningDecision decision =
                screeningEngine.evaluate(
                        session.getSignalState(),
                        emergency
                );

        /* ===============================
           5️⃣ Crisis short-circuit
           =============================== */

        if (decision.isEmergency()) {

            log.warn("Emergency detected during screening | sessionId={}", session.getSessionId());

            session.markCompleted(
                    new ScreeningResult(
                            null,
                            null,
                            "Emergency risk detected"
                    )
            );

            // ✅ Cleanup Redis session
            sessionRepository.delete(session.getSessionId());

            return new ScreeningResponseDTO(
                    "CRISIS",
                    session.getSessionId(),
                    null,
                    correlationId,
                    null,
                    "If you are in immediate danger please contact emergency services."
            );
        }

        /* ===============================
           6️⃣ Screening completed
           =============================== */

        if (decision.isReady()) {

            log.info(
                    "Screening completed | sessionId={} | questionnaire={}",
                    session.getSessionId(),
                    decision.getQuestionnaireId()
            );

            ScreeningResult result =
                    new ScreeningResult(
                            decision.getQuestionnaireId(),
                            null,
                            "Routing based on detected signals"
                    );

            session.markCompleted(result);

            // ✅ Cleanup Redis session
            sessionRepository.delete(session.getSessionId());

            ScreeningDecisionDTO dto =
                    new ScreeningDecisionDTO(
                            decision.getQuestionnaireId(),
                            decision.getConfidence(),
                            decision.isEmergency(),
                            decision.getMatchedSignals()
                    );

            return new ScreeningResponseDTO(
                    "SCREENING_COMPLETED",
                    session.getSessionId(),
                    null,
                    correlationId,
                    dto,
                    null
            );
        }

        /* ===============================
           7️⃣ Ask next question
           =============================== */

        QuestionDTO nextQuestion =
                questionSelector.nextQuestion(session);

        sessionRepository.save(session);

        return new ScreeningResponseDTO(
                "SCREENING_IN_PROGRESS",
                session.getSessionId(),
                nextQuestion,
                correlationId,
                null,
                null
        );
    }
}