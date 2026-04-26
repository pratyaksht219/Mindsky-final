package com.example.mentalhealth.session;

public interface SessionStore {
    SessionState getOrCreateSession(String sessionId);
    void save(SessionState session);
    void delete(String sessionId);
}
