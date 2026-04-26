package com.example.mentalhealth.scoring.implementation;

import com.example.mentalhealth.answers.AnswerStore;
import com.example.mentalhealth.scoring.FormulaEngines.LSASFormulaEngine;
import com.example.mentalhealth.Assessment.AssessmentResult;
import com.example.mentalhealth.scoring.ScoringService;
import com.example.mentalhealth.scoring.DTO.InterpretationRuleDTO;
import com.example.mentalhealth.scoring.DTO.ScoringDTO;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class LSASScoringService implements ScoringService {

    @Override
    public String supportedQuestionnaireId() {
        return "lsas";
    }

    @Override
    public AssessmentResult score(
            AnswerStore answers,
            ScoringDTO scoringDefinition
    ) {

        /* 1️⃣ Component scores via Formula Engine */
        double fear = LSASFormulaEngine.fearTotal(answers);
        double avoidance = LSASFormulaEngine.avoidanceTotal(answers);

        Map<String, Double> componentScores = Map.of(
                "fear_total", fear,
                "avoidance_total", avoidance
        );

        /* 2️⃣ Global score */
        double totalScore = fear + avoidance;

        /* 3️⃣ Interpretation */
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
                                        "No interpretation rule matched LSAS score: "
                                                + totalScore
                                ));

        /* 4️⃣ Result */
        return new AssessmentResult(
                scoringDefinition.getQuestionnaireId(),
                totalScore,
                rule.getLabel(),
                rule.getDescription(),
                componentScores
        );
    }
}