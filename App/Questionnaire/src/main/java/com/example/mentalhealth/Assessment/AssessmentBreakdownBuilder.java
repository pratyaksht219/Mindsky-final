package com.example.mentalhealth.Assessment;

import com.example.mentalhealth.answers.AnswerStore;
import com.example.mentalhealth.questionnaire.DTO.QuestionnaireDTO;
import com.example.mentalhealth.scoring.DTO.ScoringDTO;

public interface AssessmentBreakdownBuilder {

    AssessmentBreakdown build(
            QuestionnaireDTO questionnaire,
            ScoringDTO scoring,
            AnswerStore answers,
            AssessmentResult result);
}
