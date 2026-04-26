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
public class PHQ9ScoringService implements ScoringService {

    @Override
    public String supportedQuestionnaireId() {
        return "phq9";
    }

    @Override
    public AssessmentResult score(
            AnswerStore answers,
            ScoringDTO scoringDefinition
    ) {

        /* ---------------------------------------
         * Compute total score
         * --------------------------------------- */
        double sum = 0;

        for (ResponseMappingDTO response : scoringDefinition.getResponses()) {
            sum += answers.getDouble(
                    response.getQuestionId(),
                    response.getResponseKey()
            ).orElseThrow(() ->
                    new IllegalStateException(
                            "Missing PHQ-9 response for question="
                                    + response.getQuestionId()
                                    + ", key="
                                    + response.getResponseKey()
                    )
            );
        }

        /*  FREEZE VALUE FOR LAMBDAS */
        final double finalScore = sum;

        /* ---------------------------------------
         *  Component scores
         * --------------------------------------- */
        Map<String, Double> componentScores = new HashMap<>();
        componentScores.put("depression_severity_sum", finalScore);

        /* ---------------------------------------
         *  Interpretation
         * --------------------------------------- */
        InterpretationRuleDTO matchedRule =
                scoringDefinition.getGlobalScore()
                        .getInterpretation()
                        .getRules()
                        .stream()
                        .filter(rule ->
                                finalScore >= rule.getMin()
                                        && finalScore <= rule.getMax()
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "No interpretation rule matched PHQ-9 score: "
                                                + finalScore
                                )
                        );
        boolean suicideRisk =
                answers.getInt("phq9_q9", "value").orElse(0) > 0;

        componentScores.put("suicide_risk_flag", suicideRisk ? 1.0 : 0.0);

        /* ---------------------------------------
         *  Final Result
         * --------------------------------------- */
        return new AssessmentResult(
                scoringDefinition.getQuestionnaireId(),
                finalScore,
                matchedRule.getLabel(),
                matchedRule.getDescription(),
                componentScores
        );
    }
}