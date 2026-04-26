package com.example.mentalhealth.questionnaire.factory;

import com.example.mentalhealth.questionnaire.QuestionnaireService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class QuestionnaireServiceFactoryImpl implements QuestionnaireServiceFactory {

    private final Map<String, QuestionnaireService> registry = new HashMap<>();
    private final List<QuestionnaireService> services;

    public QuestionnaireServiceFactoryImpl(
            List<QuestionnaireService> services
    ) {
        this.services = services;
    }

    @PostConstruct
    public void initialize() {
        for (QuestionnaireService service : services) {

            String questionnaireId = service.supportedQuestionnaireId();

            if (questionnaireId == null || questionnaireId.isBlank()) {
                throw new IllegalStateException(
                        "QuestionnaireService " + service.getClass().getSimpleName()
                                + " returned empty supportedQuestionnaireId()"
                );
            }

            if (registry.containsKey(questionnaireId)) {
                throw new IllegalStateException(
                        "Duplicate QuestionnaireService for questionnaire_id: " + questionnaireId
                );
            }

            registry.put(questionnaireId, service);
        }
    }

    @Override
    public QuestionnaireService getService(String questionnaireId) {
        QuestionnaireService service = registry.get(questionnaireId);

        if (service == null) {
            throw new IllegalArgumentException(
                    "No QuestionnaireService registered for questionnaire_id: "
                            + questionnaireId
            );
        }

        return service;
    }
}