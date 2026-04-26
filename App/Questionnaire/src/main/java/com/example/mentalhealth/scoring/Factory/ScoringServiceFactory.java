package com.example.mentalhealth.scoring.Factory;

import com.example.mentalhealth.scoring.ScoringService;

public interface ScoringServiceFactory {
    ScoringService getForQuestionnaire(String questionnaireId);
}
