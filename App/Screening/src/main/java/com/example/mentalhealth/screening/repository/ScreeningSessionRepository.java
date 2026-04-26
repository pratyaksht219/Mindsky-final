package com.example.mentalhealth.screening.repository;

import com.example.mentalhealth.screening.domain.ScreeningSession;

import java.util.Optional;

public interface ScreeningSessionRepository {

    ScreeningSession save(ScreeningSession session);

    Optional<ScreeningSession> findById(String sessionId);

    void delete(String sessionId);
}