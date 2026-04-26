package com.example.mentalhealth.scoring.implementation;

import com.example.mentalhealth.answers.AnswerStore;
import com.example.mentalhealth.Assessment.AssessmentResult;
import com.example.mentalhealth.scoring.DTO.InterpretationRuleDTO;
import com.example.mentalhealth.scoring.DTO.ResponseMappingDTO;
import com.example.mentalhealth.scoring.DTO.ScoringDTO;
import com.example.mentalhealth.scoring.ScoringService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GAD7ScoringService implements ScoringService {

    private static final String COMPONENT_ID = "total_anxiety_score";

    @Override
    public String supportedQuestionnaireId() {
        return "gad7";
    }

    @Override
    public AssessmentResult score(
            AnswerStore answers,
            ScoringDTO scoringDefinition
    ) {

        /* ---------------------------------------
         *  Component score (GAD-7 total)
         * --------------------------------------- */

        double anxietyTotal = 0;

        for (ResponseMappingDTO response : scoringDefinition.getResponses()) {

            double value = answers.getDouble(
                    response.getQuestionId(),
                    response.getResponseKey()
            ).orElseThrow(() ->
                    new IllegalStateException(
                            "Missing GAD-7 response for "
                                    + response.getQuestionId()
                                    + " / "
                                    + response.getResponseKey()
                    )
            );

            anxietyTotal += value;
        }

        Map<String, Double> componentScores = new HashMap<>();
        componentScores.put(COMPONENT_ID, anxietyTotal);

        /* ---------------------------------------
         *  Global score = component score
         * --------------------------------------- */

        final double totalScore = anxietyTotal;

        /* ---------------------------------------
         * Interpretation
         * --------------------------------------- */

        InterpretationRuleDTO matchedRule =
                scoringDefinition.getGlobalScore()
                        .getInterpretation()
                        .getRules()
                        .stream()
                        .filter(rule ->
                                totalScore >= rule.getMin()
                                        && totalScore <= rule.getMax()
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "No interpretation rule matched GAD-7 score: "
                                                + totalScore
                                ));

        /* ---------------------------------------
         *  Immutable result
         * --------------------------------------- */

        return new AssessmentResult(
                scoringDefinition.getQuestionnaireId(),
                totalScore,
                matchedRule.getLabel(),
                matchedRule.getDescription(),
                componentScores
        );
    }
}