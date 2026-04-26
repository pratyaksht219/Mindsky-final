package com.example.mentalhealth.session;

import com.example.mentalhealth.answers.InMemoryAnswerStore;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryQuestionnaireSessionRegistry
        implements QuestionnaireSessionRegistry {

    private final Map<String, QuestionnaireSession> sessions =
            new ConcurrentHashMap<>();

    @Override
    public QuestionnaireSession create(String questionnaireId) {
        String sessionId = UUID.randomUUID().toString();

        QuestionnaireSession session = new QuestionnaireSession(
                sessionId,
                questionnaireId,
                new InMemoryAnswerStore()
        );

        sessions.put(sessionId, session);
        return session;
    }

    @Override
    public QuestionnaireSession get(String sessionId) {
        QuestionnaireSession session = sessions.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException(
                    "Invalid or expired session_id: " + sessionId
            );
        }
        return session;
    }

    @Override
    public void complete(String sessionId) {
        QuestionnaireSession session = get(sessionId);
        session.setCompleted(true);
        sessions.remove(sessionId); // optional: keep or discard
    }

    @Override
    public boolean exists(String sessionId) {
        return sessions.containsKey(sessionId);
    }


    @Override
    public void remove(String sessionId) {

    }
}