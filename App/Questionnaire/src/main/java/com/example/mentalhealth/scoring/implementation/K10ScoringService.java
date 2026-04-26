package com.example.mentalhealth.scoring.implementation;

import com.example.mentalhealth.answers.AnswerStore;
import com.example.mentalhealth.Assessment.AssessmentResult;
import com.example.mentalhealth.scoring.ScoringService;
import com.example.mentalhealth.scoring.DTO.InterpretationRuleDTO;
import com.example.mentalhealth.scoring.DTO.ResponseMappingDTO;
import com.example.mentalhealth.scoring.DTO.ScoringDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class K10ScoringService implements ScoringService {

    private static final String COMPONENT_ID = "distress_total";

    @Override
    public String supportedQuestionnaireId() {
        return "k10";
    }

    @Override
    public AssessmentResult score(
            AnswerStore answers,
            ScoringDTO scoringDefinition
    ) {

        /* ---------------------------------------
         *  Component score (K10 total distress)
         * --------------------------------------- */

        double distressTotal = 0;

        for (ResponseMappingDTO mapping : scoringDefinition.getResponses()) {

            double value = answers.getDouble(
                    mapping.getQuestionId(),
                    mapping.getResponseKey()
            ).orElseThrow(() ->
                    new IllegalStateException(
                            "Missing K10 response for "
                                    + mapping.getQuestionId()
                                    + " / "
                                    + mapping.getResponseKey()
                    )
            );

            distressTotal += value;
        }

        Map<String, Double> componentScores = new HashMap<>();
        componentScores.put(COMPONENT_ID, distressTotal);

        /* ---------------------------------------
         * Global score = component score
         * --------------------------------------- */

        final double totalScore = distressTotal;

        /* ---------------------------------------
         *  Interpretation
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
                                        "No interpretation rule matched K10 score: "
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