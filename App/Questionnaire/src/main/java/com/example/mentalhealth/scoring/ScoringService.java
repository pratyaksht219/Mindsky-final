package com.example.mentalhealth.scoring;

import com.example.mentalhealth.answers.AnswerStore;
import com.example.mentalhealth.Assessment.AssessmentResult;
import com.example.mentalhealth.scoring.DTO.ScoringDTO;

public interface ScoringService {

    /**
     * @return questionnaire_id this scorer supports
     */
    String supportedQuestionnaireId();

    AssessmentResult score(
            AnswerStore answers,
            ScoringDTO scoringDefinition
    );
}