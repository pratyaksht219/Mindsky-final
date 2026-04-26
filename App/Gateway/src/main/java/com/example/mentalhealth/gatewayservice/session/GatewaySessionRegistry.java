package com.example.mentalhealth.gatewayservice.session;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GatewaySessionRegistry {

    private final Map<String, GatewaySession> sessions = new ConcurrentHashMap<>();

    public GatewaySession create(String screeningSessionId) {

        GatewaySession session = new GatewaySession();

        session.setSessionId(UUID.randomUUID().toString());
        session.setScreeningSessionId(screeningSessionId);
        session.setPhase(GatewayPhase.SCREENING);

        sessions.put(session.getSessionId(), session);

        return session;
    }

    public GatewaySession get(String sessionId) {
        return sessions.get(sessionId);
    }

    public void remove(String sessionId) {
        sessions.remove(sessionId);
    }
}