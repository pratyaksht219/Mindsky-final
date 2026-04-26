package com.example.mentalhealth.screening.domain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.UUID;

@Data
@Slf4j
public class ScreeningSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sessionId;

    private String correlationId;

    private int turnCount;

    private boolean completed;

    private ScreeningSignalState signalState;

    private ScreeningResult result;

    public ScreeningSession() {
    }

    /**
     * Factory method to create a new screening session
     */
    public static ScreeningSession create(String correlationId) {

        ScreeningSession session = new ScreeningSession();

        session.sessionId = UUID.randomUUID().toString();
        session.correlationId = correlationId;
        session.turnCount = 0;
        session.completed = false;
        session.signalState = new ScreeningSignalState();

        log.info(
                "Created new screening session | sessionId={} | correlationId={}",
                session.sessionId,
                correlationId
        );

        return session;
    }

    /**
     * Increment screening turn
     */
    public void incrementTurn() {

        this.turnCount++;
        signalState.incrementTurn();
        log.debug(
                "Screening turn incremented | sessionId={} | turn={}",
                sessionId,
                turnCount
        );
    }

    /**
     * Mark screening session completed
     */
    public void markCompleted(ScreeningResult result) {

        this.completed = true;
        this.result = result;

        log.info(
                "Screening session completed | sessionId={} | questionnaire={}",
                sessionId,
                result != null ? result.getQuestionnaireId() : "NONE"
        );
    }
}