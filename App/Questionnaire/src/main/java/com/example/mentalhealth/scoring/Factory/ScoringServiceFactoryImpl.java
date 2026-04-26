package com.example.mentalhealth.scoring.Factory;

import com.example.mentalhealth.scoring.ScoringService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ScoringServiceFactoryImpl implements ScoringServiceFactory {

    private final Map<String, ScoringService> servicesByQuestionnaire;

    public ScoringServiceFactoryImpl(List<ScoringService> services) {
        this.servicesByQuestionnaire = services.stream()
                .collect(Collectors.toMap(
                        ScoringService::supportedQuestionnaireId,
                        Function.identity()
                ));
    }

    @Override
    public ScoringService getForQuestionnaire(String questionnaireId) {
        ScoringService service = servicesByQuestionnaire.get(questionnaireId);
        if (service == null) {
            throw new IllegalStateException(
                    "No ScoringService found for questionnaire: " + questionnaireId
            );
        }
        return service;
    }
}