package com.example.mentalhealth.session;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@NoArgsConstructor
public class SessionState extends  Object implements Serializable{
    private static final long serialVersionUID = 1L;

    private String sessionId;
    private SessionPhase phase = SessionPhase.INIT;



    private QuestionnaireSessionState questionnaire =
            new QuestionnaireSessionState();

    private Instant lastUpdated = Instant.now();

    public SessionState(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public SessionPhase getPhase() {
        return phase;
    }

    public void setPhase(SessionPhase phase) {
        this.phase = phase;
        touch();
    }


    public QuestionnaireSessionState getQuestionnaire() {
        return questionnaire;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    private void touch() {
        this.lastUpdated = Instant.now();
    }
}