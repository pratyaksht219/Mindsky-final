package com.example.mentalhealth.session;

public interface QuestionnaireSessionRegistry {
    QuestionnaireSession create(String questionnaireId);

    QuestionnaireSession get(String sessionId);

    void complete(String sessionId);

    boolean exists(String sessionId);


    void remove(String sessionId);
}
