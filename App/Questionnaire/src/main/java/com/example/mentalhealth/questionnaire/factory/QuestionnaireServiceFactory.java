package com.example.mentalhealth.questionnaire.factory;

import com.example.mentalhealth.questionnaire.QuestionnaireService;

public interface QuestionnaireServiceFactory {
    QuestionnaireService getService(String questionnaireId);
}
