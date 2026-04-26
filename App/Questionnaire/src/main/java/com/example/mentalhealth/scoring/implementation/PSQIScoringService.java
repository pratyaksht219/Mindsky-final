package com.example.mentalhealth.scoring.implementation;

import com.example.mentalhealth.answers.AnswerStore;
import com.example.mentalhealth.Assessment.AssessmentResult;
import com.example.mentalhealth.scoring.FormulaEngines.PSQIFormulaEngine;
import com.example.mentalhealth.scoring.ScoringService;
import com.example.mentalhealth.scoring.DTO.InterpretationRuleDTO;
import com.example.mentalhealth.scoring.DTO.ScoringDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PSQIScoringService implements ScoringService {

    @Override
    public String supportedQuestionnaireId() {
        return "psqi";
    }

    @Override
    public AssessmentResult score(
            AnswerStore answers,
            ScoringDTO scoringDefinition
    ) {

        /* ===============================
           1️⃣ Delegate to FormulaEngine
           =============================== */

        int c1 = PSQIFormulaEngine.subjectiveSleepQuality(answers);
        int c2 = PSQIFormulaEngine.sleepLatency(answers);
        int c3 = PSQIFormulaEngine.sleepDuration(answers);
        int c4 = PSQIFormulaEngine.habitualSleepEfficiency(answers);
        int c5 = PSQIFormulaEngine.sleepDisturbances(answers);
        int c6 = PSQIFormulaEngine.sleepingMedication(answers);
        int c7 = PSQIFormulaEngine.daytimeDysfunction(answers);

        int globalScore = PSQIFormulaEngine.globalScore(answers);

        /* ===============================
           2️⃣ Component Map
           =============================== */

        Map<String, Double> componentScores = new HashMap<>();
        componentScores.put("comp1_subjective_quality", (double) c1);
        componentScores.put("comp2_sleep_latency", (double) c2);
        componentScores.put("comp3_sleep_duration", (double) c3);
        componentScores.put("comp4_sleep_efficiency", (double) c4);
        componentScores.put("comp5_sleep_disturbances", (double) c5);
        componentScores.put("comp6_sleeping_medication", (double) c6);
        componentScores.put("comp7_daytime_dysfunction", (double) c7);

        /* ===============================
           3️⃣ Interpretation
           =============================== */

        InterpretationRuleDTO rule =
                scoringDefinition.getGlobalScore()
                        .getInterpretation()
                        .getRules()
                        .stream()
                        .filter(r ->
                                globalScore >= r.getMin()
                                        && globalScore <= r.getMax()
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "No PSQI interpretation matched score: "
                                                + globalScore
                                ));

        /* ===============================
           4️⃣ Return Result
           =============================== */

        return new AssessmentResult(
                scoringDefinition.getQuestionnaireId(),
                globalScore,
                rule.getLabel(),
                rule.getDescription(),
                componentScores
        );
    }
}