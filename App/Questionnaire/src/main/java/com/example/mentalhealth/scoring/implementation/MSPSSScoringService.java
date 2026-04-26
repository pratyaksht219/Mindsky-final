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
public class MSPSSScoringService implements ScoringService {

    @Override
    public String supportedQuestionnaireId() {
        return "mspss";
    }

    @Override
    public AssessmentResult score(
            AnswerStore answers,
            ScoringDTO scoringDefinition
    ) {

        /* ---------------------------------------
         *  Component scores (MSPSS Subscales)
         *  SO = q1, q2, q5, q10
         *  Fam = q3, q4, q8, q11
         *  Fri = q6, q7, q9, q12
         * --------------------------------------- */

        double soScore = average(answers, "mspss_q1", "mspss_q2", "mspss_q5", "mspss_q10");
        double famScore = average(answers, "mspss_q3", "mspss_q4", "mspss_q8", "mspss_q11");
        double friScore = average(answers, "mspss_q6", "mspss_q7", "mspss_q9", "mspss_q12");

        Map<String, Double> componentScores = new HashMap<>();
        componentScores.put("so_subscale", soScore);
        componentScores.put("fam_subscale", famScore);
        componentScores.put("fri_subscale", friScore);

        /* ---------------------------------------
         *  Global score = average of all 12 items
         * --------------------------------------- */
        double totalSum = 0;
        for (ResponseMappingDTO response : scoringDefinition.getResponses()) {
            totalSum += answers.getDouble(
                    response.getQuestionId(),
                    response.getResponseKey()
            ).orElseThrow(() ->
                    new IllegalStateException(
                            "Missing MSPSS response for " + response.getQuestionId()
                    )
            );
        }
        double globalScore = totalSum / 12.0;
        
        // Round to 1 decimal place to safely match the JSON interpretation boundary 
        // Example: JSON ranges are [1, 2.9], [3, 5], [5.1, 7], fractional gaps like 5.08 should round to 5.1
        double roundedScore = Math.round(globalScore * 10.0) / 10.0;

        /* ---------------------------------------
         * Interpretation
         * --------------------------------------- */

        InterpretationRuleDTO matchedRule =
                scoringDefinition.getGlobalScore()
                        .getInterpretation()
                        .getRules()
                        .stream()
                        .filter(rule ->
                                roundedScore >= rule.getMin()
                                        && roundedScore <= rule.getMax()
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "No interpretation rule matched MSPSS score: "
                                                + roundedScore
                                ));

        /* ---------------------------------------
         *  Immutable result
         * --------------------------------------- */

        return new AssessmentResult(
                scoringDefinition.getQuestionnaireId(),
                globalScore, // return the precise score
                matchedRule.getLabel(),
                matchedRule.getDescription(),
                componentScores
        );
    }

    private double average(AnswerStore answers, String... questionIds) {
        double sum = 0;
        for (String qId : questionIds) {
            sum += answers.getDouble(qId, "value").orElseThrow(() ->
                new IllegalStateException("Missing MSPSS response for " + qId)
            );
        }
        return sum / questionIds.length;
    }
}
