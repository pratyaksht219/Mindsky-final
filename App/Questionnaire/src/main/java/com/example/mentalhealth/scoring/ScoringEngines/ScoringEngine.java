package com.example.mentalhealth.scoring.ScoringEngines;


import com.example.mentalhealth.answers.AnswerStore;
import com.example.mentalhealth.dto.ScoringOutcome;

public interface ScoringEngine {

    ScoringOutcome score(
            String questionnaireId,
            AnswerStore answers
    );
}