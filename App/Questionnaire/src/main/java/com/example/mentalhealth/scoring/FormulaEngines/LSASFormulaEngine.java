package com.example.mentalhealth.scoring.FormulaEngines;

import com.example.mentalhealth.answers.AnswerStore;

public final class LSASFormulaEngine {

    private static final String FEAR_KEY = "fear_anxiety_0_3";
    private static final String AVOIDANCE_KEY = "avoidance_0_3";

    private LSASFormulaEngine() {}

    /* -----------------------------
       Fear / Anxiety Subtotal
       ----------------------------- */
    public static double fearTotal(AnswerStore answers) {
        return answers.getAll().values().stream()
                .mapToDouble(m ->
                        ((Number) m.getOrDefault(FEAR_KEY, 0)).doubleValue()
                )
                .sum();
    }

    /* -----------------------------
       Avoidance Subtotal
       ----------------------------- */
    public static double avoidanceTotal(AnswerStore answers) {
        return answers.getAll().values().stream()
                .mapToDouble(m ->
                        ((Number) m.getOrDefault(AVOIDANCE_KEY, 0)).doubleValue()
                )
                .sum();
    }

    /* -----------------------------
       Global Score
       ----------------------------- */
    public static double globalScore(AnswerStore answers) {
        return fearTotal(answers) + avoidanceTotal(answers);
    }
}