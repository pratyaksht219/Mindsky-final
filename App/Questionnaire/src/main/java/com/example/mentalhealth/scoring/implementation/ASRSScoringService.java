package com.example.mentalhealth.scoring.implementation;

import com.example.mentalhealth.answers.AnswerStore;
import com.example.mentalhealth.Assessment.AssessmentResult;
import com.example.mentalhealth.scoring.ScoringService;
import com.example.mentalhealth.scoring.DTO.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ASRSScoringService implements ScoringService {

    @Override
    public String supportedQuestionnaireId() {
        return "asrs";
    }

    @Override
    public AssessmentResult score(
            AnswerStore answers,
            ScoringDTO scoringDefinition
    ) {

        /* -------------------------------------------------
         *  Transform raw answers → evaluated responses
         * ------------------------------------------------- */
        Map<String, Double> evaluatedResponses = new HashMap<>();

        for (ResponseMappingDTO mapping : scoringDefinition.getResponses()) {

            double rawValue = answers.getDouble(
                    mapping.getQuestionId(),
                    mapping.getResponseKey()
            ).orElseThrow(() ->
                    new IllegalStateException(
                            "Missing ASRS response for "
                                    + mapping.getQuestionId()
                                    + " / "
                                    + mapping.getResponseKey()
                    ));

            double transformed = applyTransform(
                    rawValue,
                    mapping.getTransform()
            );

            evaluatedResponses.put(
                    key(mapping.getQuestionId(), mapping.getResponseKey()),
                    transformed
            );
        }

        /* -------------------------------------------------
         *  Component scores (clinical truth)
         * ------------------------------------------------- */
        Map<String, Double> componentScores = new HashMap<>();

        for (ComponentDTO component : scoringDefinition.getComponents()) {

            double componentScore = calculateComponent(
                    component.getCalculation(),
                    evaluatedResponses
            );

            componentScores.put(component.getId(), componentScore);
        }

        /* -------------------------------------------------
         * Global score (ASRS = sum of components)
         * ------------------------------------------------- */
        double totalScore = componentScores.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        /* -------------------------------------------------
         *  Interpretation
         * ------------------------------------------------- */
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
                                        "No interpretation rule matched ASRS score: "
                                                + totalScore
                                ));

        /* -------------------------------------------------
         *  Final immutable result
         * ------------------------------------------------- */
        return new AssessmentResult(
                scoringDefinition.getQuestionnaireId(),
                totalScore,
                rule.getLabel(),
                rule.getDescription(),
                componentScores
        );
    }

    /* =================================================
     * Helpers (SCORING ONLY — NEVER USED BY BREAKDOWN)
     * ================================================= */

    private String key(String questionId, String responseKey) {
        return questionId + "|" + responseKey;
    }

    private double applyTransform(
            double value,
            TransformDTO transform
    ) {

        if (transform == null || transform.getType() == null) {
            return value;
        }

        return switch (transform.getType()) {

            case "identity" -> value;

            case "bucket" -> transform.getBuckets()
                    .stream()
                    .filter(b ->
                            value >= b.getMin()
                                    && value <= b.getMax()
                    )
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "No bucket matched ASRS value: " + value
                            ))
                    .getScore();

            default ->
                    throw new IllegalArgumentException(
                            "Unsupported ASRS transform type: "
                                    + transform.getType()
                    );
        };
    }

    private double calculateComponent(
            CalculationDTO calculation,
            Map<String, Double> responses
    ) {

        return switch (calculation.getType()) {

            case "sum" -> calculation.getInputs()
                    .stream()
                    .mapToDouble(input ->
                            responses.getOrDefault(
                                    key(
                                            input.getQuestionId(),
                                            input.getResponseKey()
                                    ),
                                    0.0
                            )
                    )
                    .sum();

            case "average" -> calculation.getInputs()
                    .stream()
                    .mapToDouble(input ->
                            responses.getOrDefault(
                                    key(
                                            input.getQuestionId(),
                                            input.getResponseKey()
                                    ),
                                    0.0
                            )
                    )
                    .average()
                    .orElse(0.0);

            default ->
                    throw new IllegalArgumentException(
                            "Unsupported ASRS calculation type: "
                                    + calculation.getType()
                    );
        };
    }
}