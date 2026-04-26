package com.example.mentalhealth.scoring.implementation;

import com.example.mentalhealth.answers.AnswerStore;
import com.example.mentalhealth.scoring.FormulaEngines.PSS10ScoringEngine;
import com.example.mentalhealth.Assessment.AssessmentResult;
import com.example.mentalhealth.scoring.ScoringService;
import com.example.mentalhealth.scoring.DTO.InterpretationRuleDTO;
import com.example.mentalhealth.scoring.DTO.ScoringDTO;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PSS10ScoringService implements ScoringService {

    @Override
    public String supportedQuestionnaireId() {
        return "pss10";
    }

    @Override
    public AssessmentResult score(
            AnswerStore answers,
            ScoringDTO scoringDefinition
    ) {

        /* ---------------------------------------
         * Delegate scoring to engine
         * --------------------------------------- */
        final double totalScore =
                PSS10ScoringEngine.compute(answers);

        /* ---------------------------------------
         *  Component scores (authoritative)
         * --------------------------------------- */
        Map<String, Double> componentScores = Map.of(
                "perceived_stress_total", totalScore
        );

        /* ---------------------------------------
         * Interpretation
         * --------------------------------------- */
        InterpretationRuleDTO rule =
                scoringDefinition.getGlobalScore()
                        .getInterpretation()
                        .getRules()
                        .stream()
                        .filter(r ->
                                totalScore >= r.getMin()
                                        && totalScore <= r.getMax()
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "No interpretation rule matched PSS-10 score: "
                                                + totalScore
                                ));

        /* ---------------------------------------
         *  Final result
         * --------------------------------------- */
        return new AssessmentResult(
                scoringDefinition.getQuestionnaireId(),
                totalScore,
                rule.getLabel(),
                rule.getDescription(),
                componentScores
        );
    }
}