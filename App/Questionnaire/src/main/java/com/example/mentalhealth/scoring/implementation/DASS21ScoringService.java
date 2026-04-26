package com.example.mentalhealth.scoring.implementation;

import com.example.mentalhealth.answers.AnswerStore;
import com.example.mentalhealth.scoring.FormulaEngines.DASS21FormulaEngine;
import com.example.mentalhealth.Assessment.AssessmentResult;
import com.example.mentalhealth.scoring.ScoringService;
import com.example.mentalhealth.scoring.DTO.ComponentDTO;
import com.example.mentalhealth.scoring.DTO.ScoringDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DASS21ScoringService implements ScoringService {

    @Override
    public String supportedQuestionnaireId() {
        return "dass21";
    }

    @Override
    public AssessmentResult score(
            AnswerStore answers,
            ScoringDTO scoringDefinition
    ) {

        /* ---------------------------------------
         * Subscale scores (SINGLE SOURCE OF TRUTH)
         * --------------------------------------- */

        Map<String, Double> componentScores = new HashMap<>();

        for (ComponentDTO component : scoringDefinition.getComponents()) {

            double score = switch (component.getId()) {

                case "depression_score" ->
                        DASS21FormulaEngine.depressionScore(answers);

                case "anxiety_score" ->
                        DASS21FormulaEngine.anxietyScore(answers);

                case "stress_score" ->
                        DASS21FormulaEngine.stressScore(answers);

                default ->
                        throw new IllegalStateException(
                                "Unknown DASS-21 component: " + component.getId()
                        );
            };

            componentScores.put(component.getId(), score);
        }

        /* ---------------------------------------
         *  Global score = sum of subscales
         * --------------------------------------- */

        double totalScore = componentScores.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        /* ---------------------------------------
         *  Immutable result (NO recalculation later)
         * --------------------------------------- */

        return new AssessmentResult(
                scoringDefinition.getQuestionnaireId(),
                totalScore,
                "DASS-21 completed",
                "Depression, Anxiety, and Stress scores calculated",
                componentScores
        );
    }
}