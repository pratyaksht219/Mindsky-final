package com.example.mentalhealth.scoring.implementation;

import com.example.mentalhealth.answers.AnswerStore;
import com.example.mentalhealth.scoring.FormulaEngines.PCL5FormulaEngine;
import com.example.mentalhealth.Assessment.AssessmentResult;
import com.example.mentalhealth.scoring.ScoringService;
import com.example.mentalhealth.scoring.DTO.InterpretationRuleDTO;
import com.example.mentalhealth.scoring.DTO.ScoringDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PCL5ScoringService implements ScoringService {

    @Override
    public String supportedQuestionnaireId() {
        return "pcl5";
    }

    @Override
    public AssessmentResult score(
            AnswerStore answers,
            ScoringDTO scoringDefinition
    ) {

        /* ---------------------------------
         *  Component scores (DSM-5)
         * --------------------------------- */
        Map<String, Double> componentScores = new HashMap<>();

        double intrusion =
                PCL5FormulaEngine.criterionBIntrusionScore(answers);
        double avoidance =
                PCL5FormulaEngine.criterionCAvoidanceScore(answers);
        double cognitionMood =
                PCL5FormulaEngine.criterionDCognitionMoodScore(answers);
        double arousal =
                PCL5FormulaEngine.criterionEArousalScore(answers);

        componentScores.put("criterion_b_intrusion", intrusion);
        componentScores.put("criterion_c_avoidance", avoidance);
        componentScores.put("criterion_d_cognition_mood", cognitionMood);
        componentScores.put("criterion_e_arousal", arousal);

        /* ---------------------------------
         *  Total Severity Score (0–80)
         * --------------------------------- */
        double totalScore =
                intrusion + avoidance + cognitionMood + arousal;

        /* ---------------------------------
         * DSM-5 Provisional PTSD Check
         * --------------------------------- */
        boolean provisionalPTSD =
                PCL5FormulaEngine.hasProvisionalPTSD(answers);

        /* ---------------------------------
         * Interpretation
         * --------------------------------- */
        InterpretationRuleDTO matchedRule =
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
                                        "No interpretation rule matched PCL-5 score: "
                                                + totalScore
                                ));

        String label = provisionalPTSD
                ? "Provisional PTSD"
                : matchedRule.getLabel();

        String description = provisionalPTSD
                ? "Meets DSM-5 symptom criteria across intrusion, avoidance, cognition/mood, and arousal domains."
                : matchedRule.getDescription();

        /* ---------------------------------
         *  Immutable Result
         * --------------------------------- */
        return new AssessmentResult(
                scoringDefinition.getQuestionnaireId(),
                totalScore,
                label,
                description,
                componentScores
        );
    }
}