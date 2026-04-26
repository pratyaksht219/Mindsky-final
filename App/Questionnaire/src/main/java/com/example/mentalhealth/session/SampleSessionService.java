package com.example.mentalhealth.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//@Service
public class SampleSessionService implements SessionStore{

    private final Map<String, SessionState> sessions =
            new ConcurrentHashMap<>();


    @Override
    public SessionState getOrCreateSession(String sessionId) {
        return sessions.computeIfAbsent(
                sessionId,
                SessionState::new
        );
    }

    @Override
    public void save(SessionState session) {

    }


    @Override
    public void delete(String sessionId) {
        sessions.remove(sessionId);
    }
}